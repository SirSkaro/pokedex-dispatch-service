package skaro.pokedex.service.dispatch;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

@Component
public class GatewayEventDispatchRunner implements CommandLineRunner {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Dispatcher dispatcher;
	
	public GatewayEventDispatchRunner(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void run(String... args) throws Exception {
		dispatcher.dispatch()
			.onErrorResume(this::handleError)
			.subscribe();
	}

	private Mono<WorkRequest> handleError(Throwable error) {
		LOG.error("Error in consuming dispatch", error);
		return Mono.empty();
	}
	
}
