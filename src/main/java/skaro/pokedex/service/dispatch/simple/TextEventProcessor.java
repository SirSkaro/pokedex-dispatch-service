package skaro.pokedex.service.dispatch.simple;

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
		return client.getSettings(guildId)
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

}
