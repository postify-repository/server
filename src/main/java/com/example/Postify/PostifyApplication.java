package com.example.Postify;

import com.example.Postify.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PostifyApplication {
	public static void main(String[] args) {
		SpringApplication.run(PostifyApplication.class, args);
	}
}