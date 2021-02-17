package skaro.pokedex.service.dispatch.messaging;

import reactor.core.publisher.Flux;
import skaro.pokedex.sdk.messaging.discord.DiscordEventMessage;

public interface EventMessageReceiver<T extends DiscordEventMessage> {
	public static final String RECIEVE_METHOD_NAME = "receive";
	
	void receive(T message);
	Flux<T> streamMessages();
	
}
