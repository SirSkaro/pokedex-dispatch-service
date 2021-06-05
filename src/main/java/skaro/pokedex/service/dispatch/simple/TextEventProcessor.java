package skaro.pokedex.service.dispatch.simple;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.client.CacheFacade;
import skaro.pokedex.sdk.client.guild.GuildServiceClient;
import skaro.pokedex.sdk.client.guild.GuildSettings;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;
import skaro.pokedex.service.dispatch.EventProcessor;

@Service
public class TextEventProcessor implements EventProcessor<DiscordTextEventMessage> {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private TextParser parser;
	private GuildServiceClient client;
	private CacheFacade cacheFacade;
	private GuildSettings defaultSettings;

	public TextEventProcessor(TextParser parser, GuildServiceClient client, CacheFacade cacheFacade, GuildSettings defaultSettings) {
		this.parser = parser;
		this.client = client;
		this.cacheFacade = cacheFacade;
		this.defaultSettings = defaultSettings;
	}

	@Override
	public Mono<WorkRequest> process(DiscordTextEventMessage event) {
		return getGuildSettings(event.getGuildId())
				.flatMap(guildSettings -> prepareWorkRequest(event, guildSettings));
	}

	private Mono<GuildSettings> getGuildSettings(String guildId) {
		return Mono.just(guildId)
				.flatMap(id -> client.getSettings(id)
						.onErrorResume(IOException.class, error -> logConnectionError(error)))
				.switchIfEmpty(Mono.defer(() -> useAndCacheDefaultSettings(guildId)));
	}

	private Mono<GuildSettings> useAndCacheDefaultSettings(String guildId) {
		return Mono.fromCallable(() -> cacheFacade.cache(guildId, defaultSettings));
	}
	
	private Mono<WorkRequest> prepareWorkRequest(DiscordTextEventMessage event, GuildSettings settings) {
		return parser.parse(event, settings.getPrefix())
				.map(parsedText -> toWorkRequest(event, parsedText, settings));
	}
	
	private WorkRequest toWorkRequest(DiscordTextEventMessage event, ParsedText parsedText, GuildSettings settings) {
		WorkRequest request = new WorkRequest();
		request.setCommmand(parsedText.getCommmand());
		request.setArguments(parsedText.getArguments());
		request.setAuthorId(event.getAuthorId());
		request.setChannelId(event.getChannelId());
		request.setGuildId(event.getGuildId());
		request.setLanguage(settings.getLanguage());
		
		return request;
	}
	
	private Mono<GuildSettings> logConnectionError(IOException error) {
		LOG.error("Unable to connect to guild service. Using default guild settings", error);
		return Mono.empty();
	}

}
