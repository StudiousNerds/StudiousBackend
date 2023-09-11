package nerds.studiousTestProject.common.config;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.CustomAccessDeniedHandler;
import nerds.studiousTestProject.common.exception.CustomAuthenticationEntryPoint;
import nerds.studiousTestProject.common.filter.JwtExceptionHandlerFilter;
import nerds.studiousTestProject.common.filter.JwtAuthenticationFilter;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Spring Security 관련 설정 사항들
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Preflight request에 대해, 인증을 하지 않고 모든 요청을 허용
                .requestMatchers("studious/oauth/**", "/studious/members/signup", "/studious/members/login").permitAll()    // 일반, 소셜 회원가입 및 로그인
                .requestMatchers("/studious/studycafes/{cafeId}", "/studious/main").permitAll()
                .requestMatchers("/studious/members/logout", "/studious/members/reissue").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN")  // 로그아웃, 토큰 재발급
                .requestMatchers("/studious/mypage/**").hasAnyRole("USER", "ADMIN", "SUPER_ADMIN") // 닉네임, 비밀번호 수정 및 회원 탈퇴
                .requestMatchers("/studious/search/**").permitAll()
                .requestMatchers("/studious/valid/**").permitAll()   // 임시 허용
                .requestMatchers("/studious/studycafes/registrations").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/studious/members/email").permitAll()      // 이메일 찾기
                .requestMatchers(HttpMethod.POST, "/studious/members/password").permitAll()  // 비밀번호 찾기
                .requestMatchers("/studious/members/test").hasRole("USER")      // 테스트 용
                .requestMatchers("/payment/**").permitAll()//결제 테스트
                .requestMatchers("/studious/payments/**").permitAll()//결제 테스트
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
