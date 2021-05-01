package skaro.pokedex.service.dispatch.simple;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
		LOG.info("Starting dispatch");
		dispatcher.dispatch()
			.subscribe();
	}
	
}
