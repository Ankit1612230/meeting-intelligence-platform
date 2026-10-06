package com.meetingintelligence.meetingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ANALYSIS_REQUESTED = "analysis.requested";

    @Bean
    NewTopic analysisRequestedTopic() {
        return TopicBuilder.name(ANALYSIS_REQUESTED).partitions(1).replicas(1).build();
    }
}