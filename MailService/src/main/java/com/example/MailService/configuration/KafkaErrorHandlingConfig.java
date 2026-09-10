package com.example.MailService.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {
	
	@Bean
	DefaultErrorHandler errorHandling(KafkaTemplate<Long, Object> kafkaTemplate) {
		DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(kafkaTemplate);
		FixedBackOff backOff = new FixedBackOff(3000L, 3);
		return new DefaultErrorHandler(recover, backOff);
	}
	
}
