package skaro.pokedex.service.dispatch;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import skaro.pokedex.sdk.messaging.MessageReceiver;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;
import skaro.pokedex.service.dispatch.messaging.MessageQueueRegistrar;

@Component
public class TextCommandDispatcher implements Dispatcher {

	private MessageQueueRegistrar queueRegistrar;
	private TextParser textParser;
	private MessageReceiver<DiscordTextEventMessage> receiver;
	private Scheduler scheduler;
	
	public TextCommandDispatcher(MessageQueueRegistrar queueRegistrar, TextParser textParser, MessageReceiver<DiscordTextEventMessage> receiver, Scheduler scheduler) {
		this.queueRegistrar = queueRegistrar;
		this.textParser = textParser;
		this.receiver = receiver;
		this.scheduler = scheduler;
	}

	@Override
	public Flux<WorkRequest> dispatch() {
		return receiver.streamMessages(scheduler)
				.flatMap(textParser::parse)
				.flatMap(queueRegistrar::sendRequest);
	}

}
