package skaro.pokedex.service.dispatch.messaging;

import reactor.core.publisher.Mono;
import skaro.pokedex.sdk.messaging.dispatch.WorkRequest;

public interface WorkRequestRouter {

	Mono<WorkRequest> routeRequest(WorkRequest request);
	
}
