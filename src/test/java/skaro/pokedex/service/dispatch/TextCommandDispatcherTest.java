package skaro.pokedex.service.dispatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.MessageReceiver;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;
import skaro.pokedex.service.dispatch.messaging.WorkRequestRouter;

@ExtendWith(SpringExtension.class)
public class TextCommandDispatcherTest {

	@Mock
	private WorkRequestRouter queueRegistrar;
	@Mock
	private TextParser textParser;
	@Mock
	private MessageReceiver<DiscordTextEventMessage> receiver;
	@Mock
	private Scheduler scheduler;
	
	private TextCommandDispatcher dispatcher;
	
	
	@BeforeEach
	public void setup() {
		this.dispatcher = new TextCommandDispatcher(queueRegistrar, textParser, receiver, scheduler);
	}
	
	@Test
	public void testDispatchRegisteredCommand() { 
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		WorkRequest request = new WorkRequest();
		
		Mockito.when(receiver.streamMessages(ArgumentMatchers.any()))
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.just(request));
		Mockito.when(queueRegistrar.routeRequest(request))
			.thenReturn(Mono.just(request));
		
		StepVerifier.create(dispatcher.dispatch())
			.expectNext(request)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testDispatchNonRegisteredCommand() { 
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		WorkRequest request = new WorkRequest();
		
		Mockito.when(receiver.streamMessages(ArgumentMatchers.any()))
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.just(request));
		Mockito.when(queueRegistrar.routeRequest(request))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(dispatcher.dispatch())
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testDispatchNoParserResponse() { 
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		
		Mockito.when(receiver.streamMessages(ArgumentMatchers.any()))
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(dispatcher.dispatch())
			.expectComplete()
			.verify();
	}
	
}
