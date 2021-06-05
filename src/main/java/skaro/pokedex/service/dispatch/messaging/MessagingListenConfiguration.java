package skaro.pokedex.service.dispatch.messaging;

import static skaro.pokedex.sdk.messaging.gateway.GatewayMessagingConfiguration.GATEWAY_QUEUE_BEAN;

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

import skaro.pokedex.sdk.messaging.MessageReceiverHotStream;
import skaro.pokedex.sdk.messaging.cache.NearCacheTopicMessageListenerConfiguration;
import skaro.pokedex.sdk.messaging.gateway.GatewayMessagingConfiguration;

@Configuration
@Import({
	GatewayMessagingConfiguration.class,
	NearCacheTopicMessageListenerConfiguration.class
})
public class MessagingListenConfiguration {
	private static final String GATEWAY_MESSAGE_LISTENER_CONTAINER_BEAN = "gatewayMessageListenerContainer";
	private static final String GATEWAY_MESSAGE_LISTENER_ADAPTER_BEAN = "gatewayMessageListenerAdapter";
	
	@Bean(GATEWAY_MESSAGE_LISTENER_CONTAINER_BEAN)
	public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, 
			@Qualifier(GATEWAY_QUEUE_BEAN) Queue queue, 
			@Qualifier(GATEWAY_MESSAGE_LISTENER_ADAPTER_BEAN) MessageListenerAdapter adapter,
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
	
	@Bean(GATEWAY_MESSAGE_LISTENER_ADAPTER_BEAN)
	public MessageListenerAdapter listenerAdapter(DiscordTextEventMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, MessageReceiverHotStream.RECEIVE_METHOD_NAME);
	}

}
