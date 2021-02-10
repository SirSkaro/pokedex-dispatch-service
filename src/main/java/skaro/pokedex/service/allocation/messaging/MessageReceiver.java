package skaro.pokedex.service.allocation.messaging;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import skaro.pokedex.sdk.messaging.discord.DiscordTextEventMessage;

@Component
public class MessageReceiver {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String RECIEVE_METHOD_NAME = "receive";
	
    public void receive(DiscordTextEventMessage message) {
		LOG.info("{} says '{}' from channel {} ", message.getAuthorId(), message.getContent(), message.getChannelId());
	}
	
}
