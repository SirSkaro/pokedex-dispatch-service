package skaro.pokedex.service.dispatch.messaging;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.messaging.DispatchTopicMessagingConfiguration;

@Configuration
@Import({DispatchTopicMessagingConfiguration.class})
public class MessagingSenderConfiguration {

	@Bean
	public MessagePostProcessor messagePostProcessor() {
		return (message) -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
			message.getMessageProperties().setExpiration(Long.toString(0));
			return message;
		};
	}
	
	@Bean
	@Autowired
	public WorkRequestRouter queueRegistrar(TopicExchange topic, RabbitTemplate template, MessagePostProcessor postProcessor) {
		return new SimpleCommandTopicRouter(topic, template, postProcessor);
	}
	
}