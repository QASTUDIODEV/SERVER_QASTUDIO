package qastudio.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import qastudio.backend.global.handler.resolver.AuthResolver;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthResolver authResolver;

    public WebConfig(AuthResolver authResolver) {
        this.authResolver = authResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authResolver);
    }
}
