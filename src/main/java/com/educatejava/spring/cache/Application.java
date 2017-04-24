package com.educatejava.spring.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Configuration
@SpringBootApplication
@EnableCaching
public class Application {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Component
	static class Runner implements CommandLineRunner {
		@Autowired
		private UserRepository userRepository;
		
//		@Autowired
//		private RecursiveWatcherService recursiveWatcherService;

		public void run(String... args) throws Exception {
			log.info(".... Fetching user details");
			log.info("User 001 -->" + userRepository.findById("001"));
			log.info("User 001 -->" + userRepository.findById("001"));
			log.info("User 001 -->" + userRepository.findById("001"));
			log.info("User 002 -->" + userRepository.findById("002"));
			log.info("User 002 -->" + userRepository.findById("002"));
			log.info("User 002 -->" + userRepository.findById("002"));
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	//JDK Cache Manager
	@Primary
	@Bean
	public CacheManager jdkCacheManager() {
		return new ConcurrentMapCacheManager("users");
	}
	// EhCache Manager
	@Bean
	public CacheManager ehCacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}
	
	//JSR 107 - JCache Manager
	@Bean
	public CacheManager jCacheManager() {
		return new JCacheCacheManager();
	}
	
	//Guava Cache Manager
//	@Bean
//	public CacheManager guavaCacheManager() {
//		return new GuavaCacheManager();
//	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehcache.xml"));
		cmfb.setShared(true);
		return cmfb;
	}
}
