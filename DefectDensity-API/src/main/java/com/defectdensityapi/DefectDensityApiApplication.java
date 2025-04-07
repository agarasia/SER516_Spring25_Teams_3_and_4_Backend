package com.defectdensityapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.defectdensityapi.repository")
public class DefectDensityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DefectDensityApiApplication.class, args);
	}

}
