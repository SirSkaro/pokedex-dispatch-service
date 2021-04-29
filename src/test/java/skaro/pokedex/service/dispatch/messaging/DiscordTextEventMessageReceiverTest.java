package skaro.pokedex.service.dispatch.messaging;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

@ExtendWith(SpringExtension.class)
public class DiscordTextEventMessageReceiverTest {

	private DiscordTextEventMessageReceiver receiver;
	private Scheduler scheduler;
	
	@BeforeEach
	public void setup() {
		receiver = new DiscordTextEventMessageReceiver();
		scheduler = Schedulers.parallel();
	}
	
	@Test
	public void steamMessagesTest() {
		DiscordTextEventMessage message1 = new DiscordTextEventMessage();
		DiscordTextEventMessage message2 = new DiscordTextEventMessage();
		
		scheduler.schedule(() -> receiver.receive(message1), 100, TimeUnit.MILLISECONDS);
		scheduler.schedule(() -> receiver.receive(message2), 150, TimeUnit.MILLISECONDS);
		
		StepVerifier.create(receiver.streamMessages(scheduler))
				.expectNext(message1)
				.expectNext(message2)
				.expectTimeout(Duration.of(200, TimeUnit.MILLISECONDS.toChronoUnit()))
				.verify();
	}
	
}
