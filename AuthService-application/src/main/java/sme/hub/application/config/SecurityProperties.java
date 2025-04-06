package sme.hub.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "url")
public class SecurityProperties {
    private List<ApiPath> permit;

    @Getter
    @Setter
    public static class ApiPath {
        private String path;
        private List<String> methods;
    }
}
