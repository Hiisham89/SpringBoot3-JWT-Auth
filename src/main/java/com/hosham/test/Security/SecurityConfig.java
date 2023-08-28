package com.hosham.test.Security;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.httpBasic(Customizer.withDefaults());
		http.authorizeHttpRequests(auth->auth.anyRequest().authenticated());
		
		http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.csrf(
				csrf->csrf.disable());
		
		
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		return http.build();
	}
	
//	@Bean
//	public UserDetailsService userdetailservice() {
//		
//		var ud = new InMemoryUserDetailsManager();
//		UserDetails user = User.withUsername("ahmad").password("12345").roles("User").build();
//		ud.createUser(user);
//		return ud;
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
	public KeyPair keypair() {
	try {
		var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		return keyPairGenerator.generateKeyPair();
	}catch (Exception e) {
		throw new RuntimeException(e);
	}
	}
	@Bean
	public RSAKey rsaKey(KeyPair keypair) {
		return new RSAKey.Builder((RSAPublicKey) keypair.getPublic())
				.privateKey(keypair.getPrivate())
				.keyID(UUID.randomUUID().toString()).build();
	}
	
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsakey){
		var jwkSet = new JWKSet(rsakey);
		return (jwkSelector, context)->jwkSelector.select(jwkSet);
	}
		
	
	
	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsakey) throws JOSEException {
		return NimbusJwtDecoder.withPublicKey(rsakey.toRSAPublicKey())
				.build();
		
	}
	
	@Bean
	public JwtEncoder jwtencoder(JWKSource<SecurityContext> jwkSource) {
		
		return new NimbusJwtEncoder(jwkSource);
		
	}
	
	
	
}
