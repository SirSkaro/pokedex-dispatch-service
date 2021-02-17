package skaro.pokedex.service.dispatch;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;
import skaro.pokedex.service.dispatch.messaging.EventMessageReceiver;
import skaro.pokedex.service.dispatch.messaging.MessageQueueRegistrar;

@Component
public class TextCommandDispatcher implements Dispatcher {

	private MessageQueueRegistrar queueRegistrar;
	private TextParser textParser;
	private EventMessageReceiver<DiscordTextEventMessage> receiver;
	
	public TextCommandDispatcher(MessageQueueRegistrar queueRegistrar, TextParser textParser, EventMessageReceiver<DiscordTextEventMessage> receiver) {
		this.queueRegistrar = queueRegistrar;
		this.textParser = textParser;
		this.receiver = receiver;
	}

	@Override
	public Flux<WorkRequest> dispatch() {
		return receiver.streamMessages()
				.flatMap(textParser::parse)
				.flatMap(request -> queueRegistrar.sendRequest(request));
	}

}
