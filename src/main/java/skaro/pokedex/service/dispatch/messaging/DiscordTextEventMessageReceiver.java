package skaro.pokedex.service.dispatch.messaging;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.scheduler.Scheduler;
import skaro.pokedex.sdk.messaging.MessageReceiverHotStream;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;

@Component
public class DiscordTextEventMessageReceiver implements MessageReceiverHotStream<DiscordTextEventMessage> {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Flux<DiscordTextEventMessage> publish;
	private FluxSink<DiscordTextEventMessage> fluxSink;

	public DiscordTextEventMessageReceiver() {
		publish = Flux.create(this::setSink, OverflowStrategy.DROP);
	}
	
    public void receive(DiscordTextEventMessage message) {
		LOG.info("{} says '{}' from channel {} ", message.getAuthorId(), message.getContent(), message.getChannelId());
		fluxSink.next(message);
	}
    
    public Flux<DiscordTextEventMessage> streamMessages(Scheduler scheduler) {
    	return publish.publishOn(scheduler);
    }
    
    private void setSink(FluxSink<DiscordTextEventMessage> sink) {
    	this.fluxSink = sink;
    }
    
}
