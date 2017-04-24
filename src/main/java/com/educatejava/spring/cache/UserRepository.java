package com.educatejava.spring.cache;

public interface UserRepository {
	public UserDetails findById(String id);
}
