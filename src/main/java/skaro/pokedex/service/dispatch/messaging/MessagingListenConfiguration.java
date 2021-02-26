package skaro.pokedex.service.dispatch.messaging;

import java.util.concurrent.Executor;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.messaging.DispatchTopicMessagingConfiguration;
import skaro.pokedex.sdk.messaging.GatewayMessagingConfiguration;
import skaro.pokedex.sdk.messaging.MessageReceiver;

@Configuration
@Import({GatewayMessagingConfiguration.class, DispatchTopicMessagingConfiguration.class})
public class MessagingListenConfiguration {
	
	@Bean
	public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, 
			@Qualifier(GatewayMessagingConfiguration.GATEWAY_QUEUE_BEAN) Queue queue, 
			MessageListenerAdapter adapter,
			Executor executor) {
		DirectMessageListenerContainer listenerContainer = new DirectMessageListenerContainer();
		listenerContainer.setConnectionFactory(connectionFactory);
		listenerContainer.setAcknowledgeMode(AcknowledgeMode.NONE);
		listenerContainer.setQueues(queue);
		listenerContainer.setDefaultRequeueRejected(false);
		listenerContainer.setShutdownTimeout(100);
		listenerContainer.setMessageListener(adapter);
		listenerContainer.setTaskExecutor(executor);

		return listenerContainer;
	}
	
	@Bean
	public MessageListenerAdapter listenerAdapter(DiscordTextEventMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, MessageReceiver.RECIEVE_METHOD_NAME);
	}

}