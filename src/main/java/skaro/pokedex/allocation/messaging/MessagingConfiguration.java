package skaro.pokedex.allocation.messaging;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {
	public static final String MESSAGE_CONTAINER_FACTORY_BEAN = "connectionFactoryBean";

	@Bean
	public Queue newMessageQueue() {
		return new Queue("foo");
	}

	@Bean
	public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, Queue queue, MessageListenerAdapter adapter) {
		DirectMessageListenerContainer listenerContainer = new DirectMessageListenerContainer();
		listenerContainer.setConnectionFactory(connectionFactory);
		listenerContainer.setAcknowledgeMode(AcknowledgeMode.NONE);
		listenerContainer.setQueues(queue);
		listenerContainer.setDefaultRequeueRejected(false);
		listenerContainer.setShutdownTimeout(100);
		listenerContainer.setMessageListener(adapter);

		return listenerContainer;
	}
	
	@Bean
	public MessageListenerAdapter listenerAdapter(MessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receive");
	}

}
