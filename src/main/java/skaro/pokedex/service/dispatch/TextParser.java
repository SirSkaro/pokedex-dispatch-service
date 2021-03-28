package skaro.pokedex.service.dispatch;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

public interface TextParser {

	Mono<WorkRequest> parse(DiscordTextEventMessage textEvent);
	
}
