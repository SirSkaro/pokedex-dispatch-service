package skaro.pokedex.service.dispatch.messaging;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class WorkerDispatchConfigurationProperties {

	private List<@NotEmpty String> workers;

	public List<String> getWorkers() {
		return workers;
	}

	public void setWorkers(List<String> workers) {
		this.workers = workers;
	}
	
	
}
