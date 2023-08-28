package com.hosham.test;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<Users, Long>{
	
	Optional<Users> findByName(String name);
}
