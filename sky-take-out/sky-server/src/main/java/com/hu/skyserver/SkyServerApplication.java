package com.hu.skyserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@Slf4j
public class SkyServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyServerApplication.class, args);
	}

}
