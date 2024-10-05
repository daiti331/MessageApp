package com.example.messageapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class MailAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailAppApplication.class, args);
	}

}
