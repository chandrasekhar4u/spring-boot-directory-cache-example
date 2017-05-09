package com.educatejava.spring.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
	
	@Cacheable(value="users")
	public UserDetails findById(String id) {
		return new UserDetails(id, "Name"+id);
	}
	private void slowResponsee() {
		try {
			long time = 5000L;
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
