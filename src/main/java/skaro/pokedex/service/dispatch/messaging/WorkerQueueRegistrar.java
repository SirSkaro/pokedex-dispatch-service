package skaro.pokedex.service.dispatch.messaging;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

public class WorkerQueueRegistrar implements MessageQueueRegistrar {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Map<String, Queue> registeredWorkerQueues;
	private RabbitTemplate template;
	private MessagePostProcessor postProcessor;
	
	public WorkerQueueRegistrar(Map<String, Queue> registeredWorkerQueues, RabbitTemplate template, MessagePostProcessor postProcessor) {
		this.registeredWorkerQueues = registeredWorkerQueues;
		this.template = template;
		this.postProcessor = postProcessor;
	}

	@Override
	public Mono<WorkRequest> sendRequest(WorkRequest request) {
		LOG.info("Got work request {} {}", request.getCommmand(), request.getArguments());
		return getQueueForCommand(request.getCommmand())
			.map(queue -> Mono.fromCallable(() -> sendRequest(queue, request)))
			.orElseGet(() -> Mono.empty());
	}
	
	private Optional<Queue> getQueueForCommand(String command) {
		return Optional.ofNullable(registeredWorkerQueues.get(command));
	}
	
	private WorkRequest sendRequest(Queue queue, WorkRequest request) {
		LOG.info("Sending work request {}, {}", request.getCommmand(), request.getArguments());
		template.convertAndSend(queue.getName(), request, postProcessor);
		return request;
	}
	
}
