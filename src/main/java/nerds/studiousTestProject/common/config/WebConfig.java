package nerds.studiousTestProject.common.config;


import nerds.studiousTestProject.common.util.LoggedInMemberResolver;
import nerds.studiousTestProject.common.util.StringToReservationSettingsStatusConverter;
import nerds.studiousTestProject.studycafe.util.StringToSortTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    /*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:8080",
                        "http://ec2-54-180-201-100.ap-northeast-2.compute.amazonaws.com:8080",
                        "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")   // 허용되는 Method
                .allowedHeaders("*")    // 허용되는 헤더
                .exposedHeaders("*")    // response의 모든 헤더 허용
                .allowCredentials(true)    // 자격증명 허용
                .maxAge(3600);   // 허용 시간
    }

     */

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoggedInMemberResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToReservationSettingsStatusConverter());
        registry.addConverter(new StringToSortTypeConverter());
    }
}
