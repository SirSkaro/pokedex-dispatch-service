package skaro.pokedex.service.dispatch;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class DispatchConfiguration {
	
	@Bean
	public Executor executor() {
		int availableThreadCount = Runtime.getRuntime().availableProcessors() * 2;
		return Executors.newFixedThreadPool(availableThreadCount);
	}
	
	@Bean
	public Scheduler scheduler(Executor executor) {
		return Schedulers.fromExecutor(executor);
	}
	
}
