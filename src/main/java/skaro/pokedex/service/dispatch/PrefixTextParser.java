package skaro.pokedex.service.dispatch;

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
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;

@Component
public class PrefixTextParser implements TextParser {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public final static String PREFIX = "!";

	@Override
	public Mono<WorkRequest> parse(DiscordTextEventMessage textEvent) {
		LOG.info("Parsing message {}", textEvent.getContent());
		
		if(!textEvent.getContent().startsWith(PREFIX)) {
			return Mono.empty();
		}
		
		return Mono.just(toWorkRequest(textEvent));
	}
	
	private WorkRequest toWorkRequest(DiscordTextEventMessage textEvent) {
		String commandWithArgumentsNoPrefix = textEvent.getContent().substring(1);
		
		WorkRequest request = new WorkRequest();
		request.setCommmand(parseCommand(commandWithArgumentsNoPrefix));
		request.setArguments(parseArguments(commandWithArgumentsNoPrefix));
		request.setAuthorId(textEvent.getAuthorId());
		request.setChannelId(textEvent.getChannelId());
		request.setGuildId(textEvent.getGuildId());
		
		return request;
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
