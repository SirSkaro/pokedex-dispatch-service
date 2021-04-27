package skaro.pokedex.service.dispatch;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordEventMessage;

public interface EventProcessor<T extends DiscordEventMessage> {
	
	Mono<WorkRequest> process(T event);
	
}
