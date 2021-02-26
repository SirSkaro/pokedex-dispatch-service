package skaro.pokedex.service.dispatch.messaging;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.worker.messaging.WorkRequest;

public interface WorkRequestRouter {

	Mono<WorkRequest> routeRequest(WorkRequest request);
	
}
