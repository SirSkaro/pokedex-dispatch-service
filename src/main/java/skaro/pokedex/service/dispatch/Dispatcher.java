package skaro.pokedex.service.dispatch;

import reactor.core.publisher.Flux;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;

public interface Dispatcher {
	
	Flux<WorkRequest> dispatch();
	
}
