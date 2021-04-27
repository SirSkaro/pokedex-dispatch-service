package skaro.pokedex.service.dispatch.simple;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;
import skaro.pokedex.service.dispatch.Dispatcher;

@Component
public class SimpleCommandDispatchRunner implements CommandLineRunner {
	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Dispatcher dispatcher;
	
	public SimpleCommandDispatchRunner(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void run(String... args) throws Exception {
		dispatcher.dispatch()
			.flatMap(workRequest -> Mono.just(workRequest)
					.onErrorResume(this::handleError))
			.subscribe();
	}

	private Mono<WorkRequest> handleError(Throwable error) {
		LOG.error("Error in consuming dispatch", error);
		return Mono.empty();
	}
	
}
