package skaro.pokedex.service.dispatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;
import skaro.pokedex.service.dispatch.messaging.EventMessageReceiver;
import skaro.pokedex.service.dispatch.messaging.MessageQueueRegistrar;

@ExtendWith(SpringExtension.class)
public class TextCommandDispatcherTest {

	@Mock
	private MessageQueueRegistrar queueRegistrar;
	@Mock
	private TextParser textParser;
	@Mock
	private EventMessageReceiver<DiscordTextEventMessage> receiver;
	
	private TextCommandDispatcher dispatcher;
	
	
	@BeforeEach
	public void setup() {
		this.dispatcher = new TextCommandDispatcher(queueRegistrar, textParser, receiver);
	}
	
	@Test
	public void testDispatchRegisteredCommand() { 
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		WorkRequest request = new WorkRequest();
		
		Mockito.when(receiver.streamMessages())
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.just(request));
		Mockito.when(queueRegistrar.sendRequest(request))
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
		
		Mockito.when(receiver.streamMessages())
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.just(request));
		Mockito.when(queueRegistrar.sendRequest(request))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(dispatcher.dispatch())
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testDispatchNoParserResponse() { 
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		
		Mockito.when(receiver.streamMessages())
			.thenReturn(Flux.just(message));
		Mockito.when(textParser.parse(message))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(dispatcher.dispatch())
			.expectComplete()
			.verify();
	}
	
}
