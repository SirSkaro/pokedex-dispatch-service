package skaro.pokedex.service.dispatch.simple;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.client.CacheFacade;
import skaro.pokedex.sdk.client.Language;
import skaro.pokedex.sdk.client.guild.GuildServiceClient;
import skaro.pokedex.sdk.client.guild.GuildSettings;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

@ExtendWith(SpringExtension.class)
public class TextEventProcessorTest {
	
	@Mock
	private TextParser parser;
	@Mock
	private GuildServiceClient client;
	@Mock
	private CacheFacade cacheFacade;
	private GuildSettings defaultSettings;
	
	private TextEventProcessor processor;
	
	@BeforeEach
	public void setup() {
		defaultSettings = new GuildSettings();
		defaultSettings.setLanguage(Language.CHINESE_SIMPMLIFIED);
		defaultSettings.setPrefix("!");
		
		processor = new TextEventProcessor(parser, client, cacheFacade, defaultSettings);
	}
	
	@Test
	public void testProcess_eventIsCommand_clientHasSettings() {
		String prefix = "%";
		String guildId = "guild id";
		String command = "my-command";
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setGuildId(guildId);
		eventMessage.setContent(prefix + command);
		ParsedText parsedText = new ParsedText();
		parsedText.setCommmand(command);
		parsedText.setArguments(List.of());
		GuildSettings customSettings = new GuildSettings();
		customSettings.setPrefix(prefix);
		
		Mockito.when(client.getSettings(guildId))
			.thenReturn(Mono.just(customSettings));
		Mockito.when(parser.parse(eventMessage, prefix))
			.thenReturn(Mono.just(parsedText));
		
		Consumer<WorkRequest> assertWorkRequestIsAsExepcted = request -> {
			Assertions.assertEquals(parsedText.getCommmand(), request.getCommmand());
			Assertions.assertEquals(guildId, request.getGuildId());
			Assertions.assertEquals(parsedText.getArguments(), request.getArguments());
		};
		
		StepVerifier.create(processor.process(eventMessage))
			.assertNext(assertWorkRequestIsAsExepcted)
			.expectComplete()
			.verify();
		
		Mockito.verify(cacheFacade, Mockito.never()).cache(any(), any(GuildSettings.class));
	}
	
	@Test
	public void testProcess_eventIsCommand_clientHasSettings_prefixNotSet() {
		String guildId = "guild id";
		String command = "my-command";
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setGuildId(guildId);
		eventMessage.setContent(defaultSettings.getPrefix() + command);
		ParsedText parsedText = new ParsedText();
		parsedText.setCommmand(command);
		parsedText.setArguments(List.of());
		GuildSettings customSettings = new GuildSettings();
		customSettings.setLanguage(Language.SPANISH);
		
		Mockito.when(client.getSettings(guildId))
			.thenReturn(Mono.just(customSettings));
		Mockito.when(parser.parse(eventMessage, defaultSettings.getPrefix()))
			.thenReturn(Mono.just(parsedText));
		
		Consumer<WorkRequest> assertWorkRequestIsAsExepcted = request -> {
			Assertions.assertEquals(parsedText.getCommmand(), request.getCommmand());
			Assertions.assertEquals(guildId, request.getGuildId());
			Assertions.assertEquals(parsedText.getArguments(), request.getArguments());
		};
		
		StepVerifier.create(processor.process(eventMessage))
			.assertNext(assertWorkRequestIsAsExepcted)
			.expectComplete()
			.verify();
		
		Mockito.verify(cacheFacade, Mockito.never()).cache(any(), any(GuildSettings.class));
	}
	
	@Test
	public void testProcess_eventIsCommand_clientDoesNotHaveSettings() {
		String guildId = "guild id";
		String command = "my-command";
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setGuildId(guildId);
		eventMessage.setContent(defaultSettings.getPrefix() + command);
		ParsedText parsedText = new ParsedText();
		parsedText.setCommmand(command);
		parsedText.setArguments(List.of());
		
		Mockito.when(client.getSettings(guildId))
			.thenReturn(Mono.empty());
		Mockito.when(parser.parse(eventMessage, defaultSettings.getPrefix()))
			.thenReturn(Mono.just(parsedText));
		Mockito.when(cacheFacade.cache(guildId, defaultSettings))
			.then(answer -> answer.getArgument(1));
		
		Consumer<WorkRequest> assertWorkRequestIsAsExepcted = request -> {
			Assertions.assertEquals(parsedText.getCommmand(), request.getCommmand());
			Assertions.assertEquals(guildId, request.getGuildId());
			Assertions.assertEquals(parsedText.getArguments(), request.getArguments());
		};
		
		StepVerifier.create(processor.process(eventMessage))
			.assertNext(assertWorkRequestIsAsExepcted)
			.expectComplete()
			.verify();
		
		Mockito.verify(cacheFacade).cache(eq(guildId), eq(defaultSettings));
	}
	
	@Test
	public void testProcess_eventIsNotCommand_clientHasSettings() {
		String guildId = "guild id";
		String message = "some user message";
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setGuildId(guildId);
		eventMessage.setContent(message);
		
		Mockito.when(client.getSettings(guildId))
			.thenReturn(Mono.empty());
		Mockito.when(parser.parse(eventMessage, defaultSettings.getPrefix()))
			.thenReturn(Mono.empty());
		Mockito.when(cacheFacade.cache(guildId, defaultSettings))
			.then(answer -> answer.getArgument(1));
		
		StepVerifier.create(processor.process(eventMessage))
		.expectComplete()
		.verify();
		
		Mockito.verify(cacheFacade).cache(eq(guildId), eq(defaultSettings));
	}
	
	@Test
	public void testProcess_eventIsNotCommand_clientDoesNotHaveSettings() {
		String prefix = "%";
		String guildId = "guild id";
		String message = "some user message";
		DiscordTextEventMessage eventMessage = new DiscordTextEventMessage();
		eventMessage.setGuildId(guildId);
		eventMessage.setContent(message);
		GuildSettings customSettings = new GuildSettings();
		customSettings.setPrefix(prefix);
		
		Mockito.when(client.getSettings(guildId))
			.thenReturn(Mono.just(customSettings));
		Mockito.when(parser.parse(eventMessage, prefix))
			.thenReturn(Mono.empty());
		
		StepVerifier.create(processor.process(eventMessage))
		.expectComplete()
		.verify();
		
		Mockito.verify(cacheFacade, Mockito.never()).cache(eq(guildId), any(GuildSettings.class));
	}
	
}
