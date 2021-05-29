package config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"repository", "endpoints", "context.logic"})
@EnableJpaRepositories("repository")
@EntityScan({"persisted"})
public class ScanConfig {

}
