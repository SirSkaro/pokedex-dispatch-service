package skaro.pokedex.allocation.messaging;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {

	private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	public static final String RECIEVE_METHOD_NAME = "receive";
	
    public void receive(String message) {
		LOG.info("Received message: {}", message);
	}
	
}
