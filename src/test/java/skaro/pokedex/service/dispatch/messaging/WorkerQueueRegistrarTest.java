package skaro.pokedex.service.dispatch.messaging;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.test.StepVerifier;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

@ExtendWith(SpringExtension.class)
public class WorkerQueueRegistrarTest {

	@Mock
	private Map<String, Queue> registeredWorkerQueues;
	@Mock
	private RabbitTemplate template;
	@Mock
	private MessagePostProcessor postProcessor;
	
	private WorkerQueueRegistrar registrar;
	
	@BeforeEach
	public void setup() {
		registrar = new WorkerQueueRegistrar(registeredWorkerQueues, template, postProcessor);
	}
	
	@Test
	public void sendRequestWorkerNotRegistered() {
		String command = "foo";
		WorkRequest request = new WorkRequest();
		request.setCommmand(command);
		
		Mockito.when(registeredWorkerQueues.get(command))
			.thenReturn(null);
		
		StepVerifier.create(registrar.sendRequest(request))
			.expectComplete()
			.verify();
	}
	
	@Test
	public void sendRequestWorkerRegistered() {
		String command = "bar";
		String queueName = "bar worker";
		Queue queue = Mockito.mock(Queue.class);
		WorkRequest request = new WorkRequest();
		request.setCommmand(command);
		
		Mockito.when(registeredWorkerQueues.get(command))
			.thenReturn(queue);
		Mockito.when(queue.getName())
			.thenReturn(queueName);
		
		StepVerifier.create(registrar.sendRequest(request))
			.expectNext(request)
			.expectComplete()
			.verify();
	}
	
}
