package nerds.studiousTestProject.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {
    @Bean
    public DefaultUriBuilderFactory defaultUriBuilderFactory() {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        return factory;
    }

    /**
     * 인증 키를 포함 한 URL을 입력 받을 때 인코딩 관련 오류가 발생하므로, WebClient 에서 URL 인코딩을 하지 않도록 하기 위해 DefaultUriBuilderFactory 객체를 추가
     * @return URL 인코딩이 되지 않는 WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().uriBuilderFactory(defaultUriBuilderFactory()).build();
    }
}
