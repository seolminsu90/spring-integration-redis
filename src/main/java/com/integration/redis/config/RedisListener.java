package com.integration.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.redis.inbound.RedisQueueMessageDrivenEndpoint;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

//https://docs.spring.io/spring-integration/reference/html/index.html
@Configuration
public class RedisListener {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName("localhost");
		redisStandaloneConfiguration.setPort(6379);

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	// Compnent :: redis:queue-inbound-channel-adapter
	@Bean
	public RedisQueueMessageDrivenEndpoint redisQueueMessageDrivenEndpoint(
			RedisConnectionFactory redisConnectionFactory) {
		RedisQueueMessageDrivenEndpoint endpoint = new RedisQueueMessageDrivenEndpoint("test.queue",
				redisConnectionFactory);
		endpoint.setOutputChannelName("postPublicationChannel");
		endpoint.setErrorChannelName("postPublicationLoggingChannel");
		endpoint.setReceiveTimeout(5000);
		endpoint.setSerializer(new StringRedisSerializer());
		return endpoint;
	}

	@Bean
	public MessageChannel postPublicationChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "postPublicationChannel")
	public MessageHandler postPublicationChannelHandler() {
		return m -> {
			System.out.println("test.queue 에 데이터가 들어가면 바로 처리해줍니다.");
			System.out.println(m.getPayload());
		};
	}

	@Filter(inputChannel = "postPublicationChannel", outputChannel = "postPublicationChannelAfter")
	public boolean channalingOnlyTestTextFilter(String str) {
		return str.equals("Test");
	}

	@Bean
	public MessageChannel postPublicationChannelAfter() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "postPublicationChannelAfter")
	public MessageHandler postPublicationChannelAfterHandler() {
		return m -> {
			System.out.println("Test 텍스트만 통과");
			System.out.println(m.getPayload());
		};
	}

	@Bean
	public MessageChannel postPublicationLoggingChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "postPublicationLoggingChannel")
	public MessageHandler postPublicationLoggingChannelHandler() {
		return m -> System.out.println("Error Logging");
	}
}
