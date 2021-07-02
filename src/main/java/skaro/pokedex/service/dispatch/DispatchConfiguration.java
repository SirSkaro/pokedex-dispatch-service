package skaro.pokedex.service.dispatch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import skaro.pokedex.sdk.cache.NearCacheConfiguration;
import skaro.pokedex.sdk.client.guild.GuildServiceClientConfiguration;
import skaro.pokedex.sdk.client.guild.GuildSettings;
import skaro.pokedex.sdk.worker.WorkerResourceConfiguration;

@Configuration
@Import({
	NearCacheConfiguration.class,
	GuildServiceClientConfiguration.class,
	WorkerResourceConfiguration.class
})
public class DispatchConfiguration {
	private static final String DEFAULT_GUILD_SETTINGS_PREFIX = "skaro.pokedex.defaults";
	
	@Bean
	@ConfigurationProperties(DEFAULT_GUILD_SETTINGS_PREFIX)
	public GuildSettings defaultGuildSettings() {
		return new GuildSettings();
	}
	
}
