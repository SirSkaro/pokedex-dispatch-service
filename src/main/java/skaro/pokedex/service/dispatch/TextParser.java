package skaro.pokedex.service.dispatch;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;

public interface TextParser {

	Mono<WorkRequest> parse(DiscordTextEventMessage textEvent);
	
}
