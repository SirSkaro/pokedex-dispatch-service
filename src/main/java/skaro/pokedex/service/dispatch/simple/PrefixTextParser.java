package skaro.pokedex.service.dispatch.simple;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

@Component
public class PrefixTextParser implements TextParser {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public Mono<ParsedText> parse(DiscordTextEventMessage textEvent, String prefix) {
		LOG.info("Parsing message {}", textEvent.getContent());
		
		if(!textEvent.getContent().startsWith(prefix)) {
			return Mono.empty();
		}
		
		return Mono.just(toWorkRequest(textEvent, prefix));
	}
	
	private ParsedText toWorkRequest(DiscordTextEventMessage textEvent, String prefix) {
		int prefixLength = prefix.length();
		String commandWithArgumentsNoPrefix = textEvent.getContent().substring(prefixLength);
		
		ParsedText parsedText = new ParsedText();
		parsedText.setCommmand(parseCommand(commandWithArgumentsNoPrefix));
		parsedText.setArguments(parseArguments(commandWithArgumentsNoPrefix));
		
		return parsedText;
	}
	
	private String parseCommand(String fullCommand) {
		String command = StringUtils.substringBefore(fullCommand, SPACE);
		return StringUtils.lowerCase(command);
	}
	
	private List<String> parseArguments(String fullCommand) {
		String argumentComponent = StringUtils.substringAfter(fullCommand, SPACE);
		if(StringUtils.isBlank(argumentComponent)) {
			return List.of();
		}
		
		String[] arguments = StringUtils.split(argumentComponent, ",");
		return Stream.of(arguments)
				.map(StringUtils::trimToEmpty)
				.map(StringUtils::lowerCase)
				.collect(Collectors.toList());
	}

}
