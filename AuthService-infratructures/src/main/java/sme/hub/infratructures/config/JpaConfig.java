package sme.hub.infratructures.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"sme.hub"})
@EntityScan(basePackages = {"sme.hub"})
public class JpaConfig {
}
