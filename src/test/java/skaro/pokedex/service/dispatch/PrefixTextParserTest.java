package skaro.pokedex.service.dispatch;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

@ExtendWith(SpringExtension.class)
public class PrefixTextParserTest {

	private PrefixTextParser parser;
	
	@BeforeEach
	public void setup() {
		parser = new PrefixTextParser();
	}
	
	@Test
	public void testParseNoPrefix() {
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent("this message does not have the magic prefix");
		
		Mono<WorkRequest> result = parser.parse(message);
		
		StepVerifier.create(result)
			.expectNextCount(0)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithNoArguments() {
		String command = "foo";
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(PrefixTextParser.PREFIX + command);
		
		Mono<WorkRequest> result = parser.parse(message);
		
		Consumer<WorkRequest> assertWorkRequestIsCommandAndHasNoArguments = workRequest -> {
			Assertions.assertEquals(command, workRequest.getCommmand());
			Assertions.assertTrue(workRequest.getArguments().isEmpty());
		};
		
		StepVerifier.create(result)
			.assertNext(assertWorkRequestIsCommandAndHasNoArguments)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithArguments() {
		String command = "bar";
		List<String> arguments = List.of("foo", "bar", "foobar");
		String fullCommand = new StringBuilder(command)
				.append(" ").append(arguments.get(0))
				.append(", ").append(arguments.get(1))
				.append(", ").append(arguments.get(2))
				.toString();
		
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(PrefixTextParser.PREFIX + fullCommand);
		
		Mono<WorkRequest> result = parser.parse(message);
		
		Consumer<WorkRequest> assertWorkRequestIsCommandAndHasArguments = workRequest -> {
			Assertions.assertEquals(command, workRequest.getCommmand());
			Assertions.assertEquals(arguments.size(), workRequest.getArguments().size());
			Assertions.assertEquals(arguments.get(0), workRequest.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1), workRequest.getArguments().get(1));
			Assertions.assertEquals(arguments.get(2), workRequest.getArguments().get(2));
		};
		
		StepVerifier.create(result)
			.assertNext(assertWorkRequestIsCommandAndHasArguments)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithCapitalizedArguments() {
		String command = "faa";
		List<String> arguments = List.of("FOO", "BAR");
		String fullCommand = new StringBuilder(command)
				.append(" ").append(arguments.get(0))
				.append(", ").append(arguments.get(1))
				.toString();
		
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(PrefixTextParser.PREFIX + fullCommand);
		
		Mono<WorkRequest> result = parser.parse(message);
		
		Consumer<WorkRequest> assertWorkRequestIsCommandAndHasLowercaseArguments = workRequest -> {
			Assertions.assertEquals(command, workRequest.getCommmand());
			Assertions.assertEquals(arguments.size(), workRequest.getArguments().size());
			Assertions.assertEquals(arguments.get(0).toLowerCase(), workRequest.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1).toLowerCase(), workRequest.getArguments().get(1));
		};
		
		StepVerifier.create(result)
			.assertNext(assertWorkRequestIsCommandAndHasLowercaseArguments)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithArgumentsWithExtraWhitespace() {
		String command = "faa";
		List<String> arguments = List.of(" foo ", "   b ar ", "\tbaz\t");
		String fullCommand = new StringBuilder(command)
				.append(" ").append(arguments.get(0))
				.append(",").append(arguments.get(1))
				.append(",").append(arguments.get(2))
				.toString();
		
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(PrefixTextParser.PREFIX + fullCommand);
		
		Mono<WorkRequest> result = parser.parse(message);
		
		Consumer<WorkRequest> assertWorkRequestIsCommandAndHasTrimmedArguments = workRequest -> {
			Assertions.assertEquals(command, workRequest.getCommmand());
			Assertions.assertEquals(arguments.size(), workRequest.getArguments().size());
			Assertions.assertEquals(arguments.get(0).trim(), workRequest.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1).trim(), workRequest.getArguments().get(1));
			Assertions.assertEquals(arguments.get(2).trim(), workRequest.getArguments().get(2));
		};
		
		StepVerifier.create(result)
			.assertNext(assertWorkRequestIsCommandAndHasTrimmedArguments)
			.expectComplete()
			.verify();
	}
	
}
