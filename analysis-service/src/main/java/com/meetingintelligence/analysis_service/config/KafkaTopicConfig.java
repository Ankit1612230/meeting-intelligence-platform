package com.meetingintelligence.analysis_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ANALYSIS_COMPLETED = "analysis.completed";

    @Bean
    NewTopic analysisCompletedTopic() {
        return TopicBuilder.name(ANALYSIS_COMPLETED).partitions(1).replicas(1).build();
    }
}