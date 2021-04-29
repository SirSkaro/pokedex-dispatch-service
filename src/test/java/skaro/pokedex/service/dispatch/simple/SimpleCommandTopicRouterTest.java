package skaro.pokedex.service.dispatch.simple;

import static org.mockito.ArgumentMatchers.eq;
import static skaro.pokedex.sdk.messaging.dispatch.DispatchTopicMessagingConfiguration.SIMPLE_COMMAND_ROUTING_PATTERN_PREFIX;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;

@ExtendWith(SpringExtension.class)
public class SimpleCommandTopicRouterTest {

	@Mock
	private TopicExchange topic;
	@Mock
	private RabbitTemplate template;
	@Mock
	private MessagePostProcessor postProcessor;
	
	private SimpleCommandTopicRouter router;
	
	@BeforeEach
	public void setup() {
		router = new SimpleCommandTopicRouter(topic, template, postProcessor);
	}
	
	@Test
	public void sendRequest() {
		String topicName = UUID.randomUUID().toString();
		Mockito.when(topic.getName()).thenReturn(topicName);
		
		String command = "bar";
		WorkRequest request = new WorkRequest();
		request.setCommmand(command);
		
		StepVerifier.create(router.routeRequest(request))
			.expectNext(request)
			.expectComplete()
			.verify();
		
		String expectedKey = SIMPLE_COMMAND_ROUTING_PATTERN_PREFIX + "." + command;
		Mockito.verify(template).convertAndSend(eq(topicName), eq(expectedKey), eq(request), eq(postProcessor));
	}
	
}
