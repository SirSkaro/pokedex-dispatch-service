package skaro.pokedex.service.dispatch.simple;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

public interface TextParser {

	Mono<ParsedText> parse(DiscordTextEventMessage textEvent, String prefix);
	
}
