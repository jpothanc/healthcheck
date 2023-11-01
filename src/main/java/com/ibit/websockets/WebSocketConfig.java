package com.ibit.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements  WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // Enables a simple in-memory message broker
        config.setApplicationDestinationPrefixes("/app");  // Prefix for messages handled by the server
        System.out.println("configureMessageBroker");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-endpoint1").setAllowedOrigins("*");
        registry.addEndpoint("/ws-endpoint")
                //.setAllowedOrigins("http://localhost:8080", "*")
                .withSockJS();
        System.out.println("registerStompEndpoints");
    }
}
