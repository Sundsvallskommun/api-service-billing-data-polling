package se.sundsvall.billingdatapolling;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import se.sundsvall.dept44.ServiceApplication;

@EnableFeignClients
@EnableCaching
@EnableScheduling
@ServiceApplication
public class Application {
	public static void main(final String... args) {
		run(Application.class, args);
	}
}
