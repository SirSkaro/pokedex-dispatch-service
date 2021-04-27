package skaro.pokedex.service.dispatch.simple;

import static skaro.pokedex.sdk.messaging.dispatch.DispatchTopicMessagingConfiguration.SIMPLE_COMMAND_ROUTING_PATTERN_PREFIX;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.service.dispatch.messaging.WorkRequestRouter;

public class SimpleCommandTopicRouter implements WorkRequestRouter {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private TopicExchange topic;
	private RabbitTemplate template;
	private MessagePostProcessor postProcessor;
	
	public SimpleCommandTopicRouter(TopicExchange topic, RabbitTemplate template, MessagePostProcessor postProcessor) {
		this.topic = topic;
		this.template = template;
		this.postProcessor = postProcessor;
	}

	@Override
	public Mono<WorkRequest> routeRequest(WorkRequest request) {
		return Mono.fromCallable(() -> sendRequestToExchange(request));
	}
	
	private WorkRequest sendRequestToExchange(WorkRequest request) {
		LOG.info("Sending work request {}, {}", request.getCommmand(), request.getArguments());
		String key = SIMPLE_COMMAND_ROUTING_PATTERN_PREFIX + "." + request.getCommmand();
		template.convertAndSend(topic.getName(), key, request, postProcessor);
		return request;
	}
	
}
