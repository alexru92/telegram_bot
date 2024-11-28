package com.example.ApartmentsSearchWithTelegramReporting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApartmentsSearchWithTelegramReportingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApartmentsSearchWithTelegramReportingApplication.class, args);
	}

}
