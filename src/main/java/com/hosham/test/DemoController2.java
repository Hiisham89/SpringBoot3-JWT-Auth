package com.hosham.test;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class DemoController2 {
	
	private JwtEncoder jwtencoder;
	@PostMapping("/demo2")
	public ResponseEntity<jwtresponse> authentication(Authentication authentication) {
		System.out.print(authentication);
		return new ResponseEntity<>(new jwtresponse(createtoken(authentication)), HttpStatus.OK);
		 
	}
	private String createtoken(Authentication authentication) {
		var claimset = JwtClaimsSet.builder()
		 .issuer("self")
		 .issuedAt(Instant.now())
		 .expiresAt(Instant.now().plusSeconds(60 * 2))
		 .subject(authentication.getName())
		 .claim("scope", createscope(authentication))
		 .build();
		 
		
		return jwtencoder.encode(JwtEncoderParameters.from(claimset)).getTokenValue();

	}
	private String createscope(Authentication authentication) {
		return authentication.getAuthorities().stream().map(a->a.getAuthority())
		.collect(Collectors.joining(" "));
		
	}

}
record jwtresponse(String token) {}