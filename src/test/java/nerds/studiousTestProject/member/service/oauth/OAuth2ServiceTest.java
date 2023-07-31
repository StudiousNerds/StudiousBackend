package nerds.studiousTestProject.member.service.oauth;

import nerds.studiousTestProject.member.dto.general.token.JwtTokenResponse;
import nerds.studiousTestProject.member.dto.oauth.signup.OAuth2AuthenticateResponse;
import nerds.studiousTestProject.member.dto.oauth.token.OAuth2TokenResponse;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.service.member.MemberService;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {
    private static final String REGISTRATION_ID_KAKAO = "KAKAO";
    private static final String REGISTRATION_ID_NAVER = "NAVER";
    private static final String REGISTRATION_ID_GOOGLE = "GOOGLE";
    private static final String CLIENT_ID = "client-1";
    private static final String CLIENT_SECRET = "secret";
    private static final String CLIENT_NAME = "Client 1";
    private static final String REDIRECT_URI = "https://example.com";
    private static final Set<String> SCOPES = Collections.unmodifiableSet(Stream.of("profile", "email").collect(Collectors.toSet()));
    private static final String AUTHORIZATION_URI = "https://provider.com/oauth2/authorization";
    private static final String AUTHORIZATION_GRANT_TYPE_VALUE = "authorization_code";
    private static final String TOKEN_URI = "https://provider.com/oauth2/token";

    @InjectMocks
    private OAuth2Service oAuth2Service;
    @Mock
    private InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    @Mock
    private MemberService memberService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private Mono<OAuth2TokenResponse> monoToken;

    @Mock
    private Mono<Map<String, Object>> monoUserInfo;

    @Test
    @DisplayName("카카오 소셜 신규 회원")
    public void 카카오_소셜_인증_신규회원() throws Exception {

        // given
        givenNewMember(REGISTRATION_ID_KAKAO, kakaoAttributes(), MemberType.KAKAO);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_KAKAO, code());

        // then
        Assertions.assertThat(response.isExist()).isFalse();
        Assertions.assertThat(response.getJwtTokenResponse()).isNull();
        Assertions.assertThat(response.getUserInfo().getProviderId()).isEqualTo(providerId());
        Assertions.assertThat(response.getUserInfo().getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(response.getUserInfo().getType()).isEqualTo(MemberType.KAKAO);
    }

    @Test
    @DisplayName("네이버 소셜 신규 회원")
    public void 네이버_소셜_인증_신규회원() throws Exception {

        // given
        givenNewMember(REGISTRATION_ID_NAVER, naverAttributes(), MemberType.NAVER);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_NAVER, code());

        // then
        Assertions.assertThat(response.isExist()).isFalse();
        Assertions.assertThat(response.getJwtTokenResponse()).isNull();
        Assertions.assertThat(response.getUserInfo().getProviderId()).isEqualTo(providerId());
        Assertions.assertThat(response.getUserInfo().getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(response.getUserInfo().getType()).isEqualTo(MemberType.NAVER);
    }

    @Test
    @DisplayName("구글 소셜 신규 회원")
    public void 구글_소셜_인증_신규회원() throws Exception {

        // given
        givenNewMember(REGISTRATION_ID_GOOGLE, googleAttributes(), MemberType.GOOGLE);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_GOOGLE, code());

        // then
        Assertions.assertThat(response.isExist()).isFalse();
        Assertions.assertThat(response.getJwtTokenResponse()).isNull();
        Assertions.assertThat(response.getUserInfo().getProviderId()).isEqualTo(providerId());
        Assertions.assertThat(response.getUserInfo().getEmail()).isEqualTo("test@test.com");
        Assertions.assertThat(response.getUserInfo().getType()).isEqualTo(MemberType.GOOGLE);
    }

    @Test
    @DisplayName("카카오 소셜 기존 회원")
    public void 카카오_소셜_인증_기존회원() throws Exception {

        // given
        givenExistingMember(REGISTRATION_ID_KAKAO, kakaoAttributes(), MemberType.KAKAO);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_KAKAO, code());

        // then
        Assertions.assertThat(response.isExist()).isTrue();
        Assertions.assertThat(response.getJwtTokenResponse().getGrantType()).isEqualTo(jwtTokenResponse().getGrantType());
        Assertions.assertThat(response.getJwtTokenResponse().getAccessToken()).isEqualTo(jwtTokenResponse().getAccessToken());
        Assertions.assertThat(response.getUserInfo()).isNull();
    }

    @Test
    @DisplayName("네이버 소셜 기존 회원")
    public void 네이버_소셜_인증_기존회원() throws Exception {

        // given
        givenExistingMember(REGISTRATION_ID_NAVER, naverAttributes(), MemberType.NAVER);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_NAVER, code());

        // then
        Assertions.assertThat(response.isExist()).isTrue();
        Assertions.assertThat(response.getJwtTokenResponse().getGrantType()).isEqualTo(jwtTokenResponse().getGrantType());
        Assertions.assertThat(response.getJwtTokenResponse().getAccessToken()).isEqualTo(jwtTokenResponse().getAccessToken());
        Assertions.assertThat(response.getUserInfo()).isNull();
    }

    @Test
    @DisplayName("구글 소셜 기존 회원")
    public void 구글_소셜_인증_기존회원() throws Exception {

        // given
        givenExistingMember(REGISTRATION_ID_GOOGLE, googleAttributes(), MemberType.GOOGLE);

        // when
        OAuth2AuthenticateResponse response = oAuth2Service.authenticate(REGISTRATION_ID_GOOGLE, code());

        // then
        Assertions.assertThat(response.isExist()).isTrue();
        Assertions.assertThat(response.getJwtTokenResponse().getGrantType()).isEqualTo(jwtTokenResponse().getGrantType());
        Assertions.assertThat(response.getJwtTokenResponse().getAccessToken()).isEqualTo(jwtTokenResponse().getAccessToken());
        Assertions.assertThat(response.getUserInfo()).isNull();
    }

    private void givenNewMember(String registrationId, Map<String, Object> attributes, MemberType type) {
        ClientRegistration provider = clientRegistration(registrationId);
        OAuth2TokenResponse oAuth2TokenResponse = oAuth2TokenResponse();

        Long providerId = providerId();
        doReturn(provider).when(inMemoryClientRegistrationRepository).findByRegistrationId(registrationId);

        doReturn(Optional.empty()).when(memberService).findByProviderIdAndType(providerId, type);

        doReturn(requestBodyUriSpec).when(webClient).post();
        doReturn(requestBodyUriSpec).when(webClient).get();

        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).uri(provider.getProviderDetails().getTokenUri());
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).uri(provider.getProviderDetails().getUserInfoEndpoint().getUri());

        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).headers(any());
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).bodyValue(any(MultiValueMap.class));
        doReturn(responseSpec).when(requestBodyUriSpec).retrieve();
        doReturn(monoToken).when(responseSpec).bodyToMono(OAuth2TokenResponse.class);
        doReturn(monoUserInfo).when(responseSpec).bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
        doReturn(oAuth2TokenResponse).when(monoToken).block();

        doReturn(attributes).when(monoUserInfo).block();
    }

    private void givenExistingMember(String registrationId, Map<String, Object> attributes, MemberType type) {
        ClientRegistration provider = clientRegistration(registrationId);
        OAuth2TokenResponse oAuth2TokenResponse = oAuth2TokenResponse();
        Member member = member(registrationId);
        JwtTokenResponse jwtTokenResponse = jwtTokenResponse();

        Long providerId = providerId();
        doReturn(provider).when(inMemoryClientRegistrationRepository).findByRegistrationId(registrationId);

        doReturn(Optional.of(member)).when(memberService).findByProviderIdAndType(providerId, type);

        doReturn(requestBodyUriSpec).when(webClient).post();
        doReturn(requestBodyUriSpec).when(webClient).get();

        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).uri(provider.getProviderDetails().getTokenUri());
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).uri(provider.getProviderDetails().getUserInfoEndpoint().getUri());

        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).headers(any());
        doReturn(requestBodyUriSpec).when(requestBodyUriSpec).bodyValue(any(MultiValueMap.class));
        doReturn(responseSpec).when(requestBodyUriSpec).retrieve();
        doReturn(monoToken).when(responseSpec).bodyToMono(OAuth2TokenResponse.class);
        doReturn(monoUserInfo).when(responseSpec).bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
        doReturn(oAuth2TokenResponse).when(monoToken).block();

        doReturn(attributes).when(monoUserInfo).block();
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateToken(member);
    }

    private String code() {
        return "code";
    }

    private OAuth2TokenResponse oAuth2TokenResponse() {
        OAuth2TokenResponse oAuth2TokenResponse = new OAuth2TokenResponse();
        oAuth2TokenResponse.setAccess_token("accessToken");
        oAuth2TokenResponse.setRefresh_token("refreshToken");
        oAuth2TokenResponse.setExpires_in(10000L);

        return oAuth2TokenResponse;
    }

    private ClientRegistration clientRegistration(String registration_id) {
         return ClientRegistration.withRegistrationId(registration_id)
                 .clientId(CLIENT_ID)
                 .clientSecret(CLIENT_SECRET)
                 .clientName(CLIENT_NAME)
                 .scope(SCOPES)
                 .authorizationUri(AUTHORIZATION_URI)
                 .authorizationGrantType(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_VALUE))
                 .tokenUri(TOKEN_URI)
                 .redirectUri(REDIRECT_URI)
                 .build();
    }

    private Long providerId() {
        return (long) "providerId".hashCode();
    }

    private Map<String, Object> kakaoAttributes() {
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "테스터");

        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", "test@test.com");
        kakaoAccount.put("profile", profile);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", providerId());
        attributes.put("kakao_account", kakaoAccount);

        return attributes;
    }

    private Map<String, Object> naverAttributes() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", providerId());
        response.put("email", "test@test.com");
        response.put("name", "테스터");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("response", response);

        return attributes;
    }

    private Map<String, Object> googleAttributes() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", providerId());
        params.put("email", "test@test.com");
        params.put("name", "테스터");
        return params;
    }

    private Member member(String registrationId) {
        return Member.builder()
                .email("test@test.com")
                .password("password")
                .type(MemberType.valueOf(registrationId))
                .phoneNumber("01090432652")
                .usable(true)
                .build();
    }

    private JwtTokenResponse jwtTokenResponse() {
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
        jwtTokenResponse.setGrantType("Bearer");
        jwtTokenResponse.setAccessToken("Test");
        return jwtTokenResponse;
    }
}