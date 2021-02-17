package skaro.pokedex.service.dispatch.messaging;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class WorkerDispatchConfigurationProperties {

	@NotNull
	private List<@NotEmpty String> workers;

	public List<String> getWorkers() {
		return workers;
	}

	public void setWorkers(List<String> workers) {
		this.workers = workers;
	}
	
	
}
