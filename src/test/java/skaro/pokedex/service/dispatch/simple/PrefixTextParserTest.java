package skaro.pokedex.service.dispatch.simple;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

@ExtendWith(SpringExtension.class)
public class PrefixTextParserTest {
	private static final String PREFIX = "!";
	
	private PrefixTextParser parser;
	
	@BeforeEach
	public void setup() {
		parser = new PrefixTextParser();
	}
	
	@Test
	public void testParseNoPrefix() {
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent("this message does not have the magic prefix");
		
		Mono<ParsedText> result = parser.parse(message, PREFIX);
		
		StepVerifier.create(result)
			.expectNextCount(0)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithNoArguments() {
		String command = "foo";
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(PREFIX + command);
		
		Mono<ParsedText> result = parser.parse(message, PREFIX);
		
		Consumer<ParsedText> assertParsedTextIsCommandAndHasNoArguments = parsedText -> {
			Assertions.assertEquals(command, parsedText.getCommmand());
			Assertions.assertTrue(parsedText.getArguments().isEmpty());
		};
		
		StepVerifier.create(result)
			.assertNext(assertParsedTextIsCommandAndHasNoArguments)
			.expectComplete()
			.verify();
	}
	
	@Test
	public void testParseCommandWithNoArguments_longPrefix() {
		String command = "foo";
		String prefix = "my-long-long-prefix$$";
		DiscordTextEventMessage message = new DiscordTextEventMessage();
		message.setContent(prefix + command);
		
		Mono<ParsedText> result = parser.parse(message, prefix);
		
		Consumer<ParsedText> assertParsedTextIsCommandAndHasNoArguments = parsedText -> {
			Assertions.assertEquals(command, parsedText.getCommmand());
			Assertions.assertTrue(parsedText.getArguments().isEmpty());
		};
		
		StepVerifier.create(result)
			.assertNext(assertParsedTextIsCommandAndHasNoArguments)
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
		message.setContent(PREFIX + fullCommand);
		
		Mono<ParsedText> result = parser.parse(message, PREFIX);
		
		Consumer<ParsedText> assertParsedTextIsCommandAndHasArguments = parsedText -> {
			Assertions.assertEquals(command, parsedText.getCommmand());
			Assertions.assertEquals(arguments.size(), parsedText.getArguments().size());
			Assertions.assertEquals(arguments.get(0), parsedText.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1), parsedText.getArguments().get(1));
			Assertions.assertEquals(arguments.get(2), parsedText.getArguments().get(2));
		};
		
		StepVerifier.create(result)
			.assertNext(assertParsedTextIsCommandAndHasArguments)
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
		message.setContent(PREFIX + fullCommand);
		
		Mono<ParsedText> result = parser.parse(message, PREFIX);
		
		Consumer<ParsedText> assertParsedTextIsCommandAndHasLowercaseArguments = parsedText -> {
			Assertions.assertEquals(command, parsedText.getCommmand());
			Assertions.assertEquals(arguments.size(), parsedText.getArguments().size());
			Assertions.assertEquals(arguments.get(0).toLowerCase(), parsedText.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1).toLowerCase(), parsedText.getArguments().get(1));
		};
		
		StepVerifier.create(result)
			.assertNext(assertParsedTextIsCommandAndHasLowercaseArguments)
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
		message.setContent(PREFIX + fullCommand);
		
		Mono<ParsedText> result = parser.parse(message, PREFIX);
		
		Consumer<ParsedText> assertParsedTextIsCommandAndHasTrimmedArguments = parsedText -> {
			Assertions.assertEquals(command, parsedText.getCommmand());
			Assertions.assertEquals(arguments.size(), parsedText.getArguments().size());
			Assertions.assertEquals(arguments.get(0).trim(), parsedText.getArguments().get(0));
			Assertions.assertEquals(arguments.get(1).trim(), parsedText.getArguments().get(1));
			Assertions.assertEquals(arguments.get(2).trim(), parsedText.getArguments().get(2));
		};
		
		StepVerifier.create(result)
			.assertNext(assertParsedTextIsCommandAndHasTrimmedArguments)
			.expectComplete()
			.verify();
	}
	
}
