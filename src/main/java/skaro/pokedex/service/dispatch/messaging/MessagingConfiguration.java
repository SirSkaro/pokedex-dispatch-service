package skaro.pokedex.service.dispatch.messaging;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.messaging.MessageReceiver;
import skaro.pokedex.sdk.messaging.GatewayMessagingConfiguration;

@Configuration
@Import(GatewayMessagingConfiguration.class)
public class MessagingConfiguration {
	private static final String WORKER_LIST_PROPERTY = "skaro.pokedex";
	
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

	@Bean
	public MessagePostProcessor messagePostProcessor() {
		return (message) -> {
			message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT);
			message.getMessageProperties().setExpiration(Long.toString(0));
			return message;
		};
	}
	
	@Bean
	@ConfigurationProperties(WORKER_LIST_PROPERTY)
	@Valid
	public WorkerDispatchConfigurationProperties workerDispatchConfigurationProperties() {
		return new WorkerDispatchConfigurationProperties();
	}
	
	@Bean
	@Autowired
	public MessageQueueRegistrar queueRegistrar(WorkerDispatchConfigurationProperties dispatchProperties, 
			RabbitTemplate template,
			MessagePostProcessor postProcessor) {
		Map<String, Queue> workerQueues = dispatchProperties.getWorkers().stream()
			.map(Queue::new)
			.collect(Collectors.toMap(Queue::getActualName, Function.identity()));
		
		return new WorkerQueueRegistrar(workerQueues, template, postProcessor);
	}
	
}
