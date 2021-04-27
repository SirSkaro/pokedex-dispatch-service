package skaro.pokedex.service.dispatch.simple;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import skaro.pokedex.sdk.messaging.MessageReceiver;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.sdk.messaging.gateway.DiscordTextEventMessage;
import skaro.pokedex.service.dispatch.Dispatcher;
import skaro.pokedex.service.dispatch.EventProcessor;
import skaro.pokedex.service.dispatch.messaging.WorkRequestRouter;

@Component
public class TextCommandDispatcher implements Dispatcher {

	private WorkRequestRouter router;
	private EventProcessor<DiscordTextEventMessage> processor;
	private MessageReceiver<DiscordTextEventMessage> receiver;
	private Scheduler scheduler;
	
	public TextCommandDispatcher(WorkRequestRouter queueRegistrar, EventProcessor<DiscordTextEventMessage> processor, MessageReceiver<DiscordTextEventMessage> receiver, Scheduler scheduler) {
		this.router = queueRegistrar;
		this.processor = processor;
		this.receiver = receiver;
		this.scheduler = scheduler;
	}

	@Override
	public Flux<WorkRequest> dispatch() {
		return receiver.streamMessages(scheduler)
				.flatMap(processor::process)
				.flatMap(router::routeRequest);
	}

}
