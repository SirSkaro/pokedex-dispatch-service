package skaro.pokedex.service.dispatch;

import reactor.core.publisher.Flux;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

public interface Dispatcher {
	
	Flux<WorkRequest> dispatch();
	
}
