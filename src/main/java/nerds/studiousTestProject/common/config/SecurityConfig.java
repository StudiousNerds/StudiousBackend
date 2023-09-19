package nerds.studiousTestProject.common.config;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.handler.CustomAccessDeniedHandler;
import nerds.studiousTestProject.common.exception.handler.CustomAuthenticationEntryPoint;
import nerds.studiousTestProject.common.exception.handler.HttpRequestEndPointChecker;
import nerds.studiousTestProject.common.filter.JwtAuthenticationFilter;
import nerds.studiousTestProject.common.filter.JwtExceptionHandlerFilter;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 관련 설정 사항들
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final HttpRequestEndPointChecker httpRequestEndPointChecker;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Preflight request에 대해, 인증을 하지 않고 모든 요청을 허용
                .requestMatchers("/studious/mypage/**", "/studious/reservations/**", "/studious/payments/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/studious/**/managements/**").hasRole("ADMIN")
                .requestMatchers("/studious/members/logout").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/studious/members/**").permitAll()
                .requestMatchers("/studious/search/**").permitAll()
                .requestMatchers("/studious/studycafes/**").permitAll()
                .requestMatchers("/studious/main").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint(httpRequestEndPointChecker))
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }

    //Cors 설정
    // Spring MVC 보다 Spring Security가 먼저 실행되므로 Cors 설정은 Security 에서 하는 것이 좋다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
//        configuration.addExposedHeader("*"); 노출할 헤더들
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
