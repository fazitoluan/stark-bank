package com.webhookcallbackreceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebhookCallbackReceiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookCallbackReceiverApplication.class, args);
	}

}
