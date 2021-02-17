package skaro.pokedex.service.dispatch.messaging;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.worker.WorkRequest;

public interface MessageQueueRegistrar {

	Mono<WorkRequest> sendRequest(WorkRequest request);
	
}
