package skaro.pokedex.service.dispatch.messaging;

import static skaro.pokedex.sdk.messaging.dispatch.DispatchTopicMessagingConfiguration.DISPATCH_TOPIC_EXCHANGE_BEAN;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.messaging.dispatch.DispatchTopicMessagingConfiguration;
import skaro.pokedex.service.dispatch.simple.SimpleCommandTopicRouter;

@Configuration
@Import({DispatchTopicMessagingConfiguration.class})
public class MessagingSendConfiguration {
	private static final String DISPATCH_MESSAGE_POST_PROCESSOR_BEAN = "dispatchMessagePostProcessor";
	
	@Bean(DISPATCH_MESSAGE_POST_PROCESSOR_BEAN)
	public MessagePostProcessor messagePostProcessor() {
		return (message) -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
			message.getMessageProperties().setExpiration(Long.toString(0));
			return message;
		};
	}
	
	@Bean
	public WorkRequestRouter workRequestRouter(
			@Qualifier(DISPATCH_TOPIC_EXCHANGE_BEAN) TopicExchange topic, 
			RabbitTemplate template, 
			@Qualifier(DISPATCH_MESSAGE_POST_PROCESSOR_BEAN) MessagePostProcessor postProcessor) {
		return new SimpleCommandTopicRouter(topic, template, postProcessor);
	}
	
}
