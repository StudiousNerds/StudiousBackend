package nerds.studiousTestProject.member.service.member;

import nerds.studiousTestProject.member.dto.general.find.FindEmailRequest;
import nerds.studiousTestProject.member.dto.general.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.general.token.JwtTokenResponse;
import nerds.studiousTestProject.member.dto.general.withdraw.WithdrawRequest;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.entity.token.LogoutAccessToken;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.service.MemberService;
import nerds.studiousTestProject.member.service.token.LogoutAccessTokenService;
import nerds.studiousTestProject.member.service.token.RefreshTokenService;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_DEFAULT_TYPE_USER;
import static nerds.studiousTestProject.support.fixture.LogoutAccessTokenFixture.FIRST_LOGOUT_ACCESS_TOKEN;
import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.MemberFixture.KAKAO_USER;
import static nerds.studiousTestProject.support.fixture.RefreshTokenFixture.FIRST_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    LogoutAccessTokenService logoutAccessTokenService;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    private Member defaultMember;
    private Member socialMember;
    private String accessToken;
    private String resolvedAccessToken;
    private RefreshToken refreshToken;
    private LogoutAccessToken logoutAccessToken;
    private JwtTokenResponse jwtTokenResponse;
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @BeforeEach
    public void beforeEach() {
        defaultMember = DEFAULT_USER.생성();
        socialMember = KAKAO_USER.생성();
        accessToken = "AccessToken";
        resolvedAccessToken = "resolvedAccessToken";
        refreshToken = FIRST_REFRESH_TOKEN.생성();
        logoutAccessToken = FIRST_LOGOUT_ACCESS_TOKEN.생성();
        jwtTokenResponse = JwtTokenResponse
                .builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .build();
    }

    @Test
    @DisplayName("일반 회원가입")
    public void 일반_회원가입() throws Exception {
        // given
        SignUpRequest signUpRequest = defaultSignUpRequest();
        Member member = defaultMember();

        doReturn(false).when(memberRepository).existsByPhoneNumber(signUpRequest.getPhoneNumber());
        doReturn("password").when(passwordEncoder).encode(signUpRequest.getPassword());
        doReturn(Optional.of(member)).when(memberRepository).findByEmailAndType(signUpRequest.getEmail(), MemberType.DEFAULT);

        // when
        memberService.register(signUpRequest);

        // then
        String email = memberRepository.findByEmailAndType(signUpRequest.getEmail(), MemberType.DEFAULT).orElseThrow(() -> new RuntimeException("일반 회원 찾기 실패")).getEmail();
        Assertions.assertThat(email).isEqualTo(signUpRequest.getEmail());
    }

    @Test
    @DisplayName("소셜 회원가입")
    public void 소셜_회원가입() throws Exception {

        // given
        SignUpRequest signUpRequest = socialSignUpRequest();
        Member member = socialMember();

        doReturn(false).when(memberRepository).existsByProviderIdAndType(signUpRequest.getProviderId(), MemberType.KAKAO);
        doReturn(false).when(memberRepository).existsByPhoneNumber(signUpRequest.getPhoneNumber());
        doReturn("password").when(passwordEncoder).encode(signUpRequest.getPassword());
        doReturn(Optional.of(member)).when(memberRepository).findByEmailAndType(signUpRequest.getEmail(), MemberType.KAKAO);

        // when
        memberService.register(signUpRequest);

        // then
        String email = memberRepository.findByEmailAndType(signUpRequest.getEmail(), MemberType.KAKAO).orElseThrow(() -> new RuntimeException("소셜 회원 찾기 실패")).getEmail();
        Assertions.assertThat(email).isEqualTo(signUpRequest.getEmail());
    }

    @Test
    @DisplayName("일반 로그인")
    public void 로그인() throws Exception {

        // given
        Member member = defaultMember();
        String email = member.getEmail();
        String password = member.getPassword();
        JwtTokenResponse jwtTokenResponse = jwtTokenResponse();

        doReturn(Collections.singletonList(member)).when(memberRepository).findByEmail(email);
        doReturn(true).when(passwordEncoder).matches(password, member.getPassword());
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateToken(member);

        // when
        JwtTokenResponse loginTokenResponse = memberService.issueToken(email, password);

        // then
        Assertions.assertThat(loginTokenResponse.getGrantType()).isEqualTo(jwtTokenResponse.getGrantType());
        Assertions.assertThat(loginTokenResponse.getAccessToken()).isEqualTo(jwtTokenResponse.getAccessToken());
    }

    @Test
    @DisplayName("통합 로그아웃")
    public void 로그아웃() throws Exception {

        // given
        String accessToken = accessToken();
        String resolvedAccessToken = resolvedAccessToken();
        LogoutAccessToken logoutAccessToken = logoutAccessToken();

        doReturn(resolvedAccessToken).when(jwtTokenProvider).resolveToken(accessToken);
        doReturn(1L).when(jwtTokenProvider).parseToken(resolvedAccessToken);
//        doReturn(10000L).when(jwtTokenProvider).getRemainTime(resolvedAccessToken);
//        doReturn(Optional.of(null)).when(logoutAccessTokenService).saveLogoutAccessToken(logoutAccessToken);

        // when
        Long memberId = memberService.expireToken(accessToken).getMemberId();

        // then
        Assertions.assertThat(memberId).isEqualTo(jwtTokenProvider.parseToken(resolvedAccessToken));
    }

    @Test
    @DisplayName("토큰 재발급")
    public void 토큰_재발급() throws Exception {

        // given

        // when

        // then
    }

    @Test
    @DisplayName("휴대폰 번호로 이메일 찾기")
    public void 휴대폰_번호로_이메일_찾기() throws Exception {

        // given
        Member member = defaultMember();
        doReturn(Optional.of(member)).when(memberRepository).findByPhoneNumber(member.getPhoneNumber());

        // when
        FindEmailRequest findEmailRequest = new FindEmailRequest();
        findEmailRequest.setPhoneNumber(member.getPhoneNumber());

        String email = memberService.findEmailFromPhoneNumber(findEmailRequest).getEmail();

        // then
        Assertions.assertThat(email).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("닉네임 수정")
    public void 닉네임_수정() throws Exception {

        // given
        String accessToken = accessToken();
        String resolvedAccessToken = resolvedAccessToken();
        Member member = defaultMember();

        doReturn(resolvedAccessToken).when(jwtTokenProvider).resolveToken(accessToken);
        doReturn(member.getId()).when(jwtTokenProvider).parseToken(resolvedAccessToken);
        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());

        // when
        PatchNicknameRequest patchNicknameRequest = new PatchNicknameRequest();
        patchNicknameRequest.setNewNickname("newNickname");
        memberService.replaceNickname(accessToken, patchNicknameRequest);

        // then
        Assertions.assertThat(member.getNickname()).isEqualTo(patchNicknameRequest.getNewNickname());
    }

    @Test
    @DisplayName("회원 탈퇴")
    @WithUserDetails("1L")
    public void 회원_탈퇴() throws Exception {

        // given
        String accessToken = accessToken();
        String resolvedAccessToken = resolvedAccessToken();
        Member member = defaultMember();
        String password = member.getPassword();
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setPassword(password);

        doReturn(resolvedAccessToken).when(jwtTokenProvider).resolveToken(accessToken);
        doReturn(member.getId()).when(jwtTokenProvider).parseToken(resolvedAccessToken);
        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(true).when(passwordEncoder).matches(any(), any());

        // when
        memberService.deactivate(accessToken, withdrawRequest);

        // then
        Assertions.assertThat(member.isEnabled()).isFalse();
    }


    private LogoutAccessToken logoutAccessToken() {
        return LogoutAccessToken.builder()
                .token("Test")
                .expiration(10000L)
                .build();
    }

    private Member defaultMember() {
        return Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .type(MemberType.DEFAULT)
                .phoneNumber("01090432652")
                .usable(true)
                .build();
    }

    private Member socialMember() {
        return Member.builder()
                .id(1L)
                .email("test@test.com")
                .password("password")
                .type(MemberType.KAKAO)
                .phoneNumber("01090432652")
                .usable(true)
                .build();
    }

    private SignUpRequest socialSignUpRequest() {
        SignUpRequest signUpRequest = signUpRequest();
        signUpRequest.setProviderId(123456L);
        signUpRequest.setType(MemberType.KAKAO);
        return signUpRequest;
    }

    private SignUpRequest defaultSignUpRequest() {
        SignUpRequest signUpRequest = signUpRequest();
        signUpRequest.setType(MemberType.DEFAULT);
        return signUpRequest;
    }

    private SignUpRequest signUpRequest() {
        return SignUpRequest.builder()
                .email("test@test.com")
                .password("123456")
                .name("김민우")
                .nickname("킹민우")
                .roles(Collections.singletonList("USER"))
                .phoneNumber("01090432652")
                .birthday(LocalDate.of(1999, 12, 18))
                .build();
    }

    private String accessToken() {
        return "Bearer Test";
    }

    private String resolvedAccessToken() {
        return "Test";
    }

    private JwtTokenResponse jwtTokenResponse() {
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
        jwtTokenResponse.setGrantType("Bearer");
        jwtTokenResponse.setAccessToken("Test");
        return jwtTokenResponse;
    }
}