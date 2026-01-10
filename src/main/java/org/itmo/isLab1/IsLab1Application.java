package org.itmo.isLab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class IsLab1Application {

	public static void main(String[] args) {
		SpringApplication.run(IsLab1Application.class, args);
	}
}
