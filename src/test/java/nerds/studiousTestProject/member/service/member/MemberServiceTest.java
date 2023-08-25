package nerds.studiousTestProject.member.service.member;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.service.StorageService;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.member.dto.general.find.FindEmailRequest;
import nerds.studiousTestProject.member.dto.general.find.FindEmailResponse;
import nerds.studiousTestProject.member.dto.general.find.FindPasswordRequest;
import nerds.studiousTestProject.member.dto.general.find.FindPasswordResponse;
import nerds.studiousTestProject.member.dto.general.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.general.token.JwtTokenResponse;
import nerds.studiousTestProject.member.dto.general.withdraw.WithdrawRequest;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.entity.token.LogoutAccessToken;
import nerds.studiousTestProject.member.entity.token.RefreshToken;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.service.MemberService;
import nerds.studiousTestProject.member.service.token.LogoutAccessTokenService;
import nerds.studiousTestProject.member.service.token.RefreshTokenService;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    StorageService storageService;

    @Mock
    TokenService tokenService;

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
        SignUpRequest request = SignUpRequest.builder()
                .email(defaultMember.getEmail())
                .password(defaultMember.getPassword())
                .type(defaultMember.getType())
                .roles(List.of(MemberRole.USER.name()))
                .build();

        doReturn(false).when(memberRepository).existsByPhoneNumber(request.getPhoneNumber());
        doReturn(defaultMember.getPassword()).when(passwordEncoder).encode(request.getPassword());
        doReturn(Optional.of(defaultMember)).when(memberRepository).findByEmailAndType(request.getEmail(), MemberType.DEFAULT);

        // when
        memberService.register(request);

        // then
        String email = memberRepository.findByEmailAndType(request.getEmail(), MemberType.DEFAULT).orElseThrow(() -> new RuntimeException("일반 회원 찾기 실패")).getEmail();
        assertThat(email).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("일반 회원가입에서 providerId는 있으면 검증에 실패")
    public void 일반_회원가입_소셜_ID_있는_경우() throws Exception {

        // given
        SignUpRequest request = SignUpRequest.builder()
                .type(MemberType.DEFAULT)
                .providerId(1234L)
                .build();

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("일반/소셜 회원가입에 필요한 파라미터가 잘못되었습니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("소셜 회원가입")
    public void 소셜_회원가입() throws Exception {

        // given
        SignUpRequest request = SignUpRequest.builder()
                .email(socialMember.getEmail())
                .password(socialMember.getPassword())
                .type(socialMember.getType())
                .providerId(1234L)
                .roles(List.of(MemberRole.USER.name()))
                .build();

        doReturn(false).when(memberRepository).existsByProviderIdAndType(request.getProviderId(), request.getType());
        doReturn(false).when(memberRepository).existsByPhoneNumber(request.getPhoneNumber());
        doReturn(socialMember.getPassword()).when(passwordEncoder).encode(request.getPassword());
        doReturn(Optional.of(socialMember)).when(memberRepository).findByEmailAndType(request.getEmail(), request.getType());

        // when
        memberService.register(request);

        // then
        String email = memberRepository.findByEmailAndType(request.getEmail(), MemberType.KAKAO).orElseThrow(() -> new RuntimeException("소셜 회원 찾기 실패")).getEmail();
        assertThat(email).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("소셜 회원가입에서 providerId가 없으면 검증에 실패")
    public void 소셜_회원가입_소셜_ID_없는_경우() throws Exception {

        // given
        SignUpRequest request = SignUpRequest.builder()
                .type(MemberType.KAKAO)
                .build();

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("일반/소셜 회원가입에 필요한 파라미터가 잘못되었습니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("소셜 회원가입에서 providerId가 없으면 검증에 실패")
    public void 소셜_회원가입_비밀번호_있는_경우() throws Exception {

        // given
        SignUpRequest request = SignUpRequest.builder()
                .type(MemberType.KAKAO)
                .password(socialMember.getPassword())
                .build();

        // when
        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(request);

        // then
        assertThat(violations.stream().anyMatch(
                error -> error.getMessage().equals("일반/소셜 회원가입에 필요한 파라미터가 잘못되었습니다.")
        )).isTrue();
    }

    @Test
    @DisplayName("일반 로그인")
    public void 로그인() throws Exception {

        // given
        String email = defaultMember.getEmail();
        String password = defaultMember.getPassword();

        doReturn(Collections.singletonList(defaultMember)).when(memberRepository).findByEmail(email);
        doReturn(true).when(passwordEncoder).matches(password, defaultMember.getPassword());
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateToken(defaultMember);

        // when
        JwtTokenResponse loginTokenResponse = memberService.issueToken(email, password);

        // then
        assertThat(loginTokenResponse.getGrantType()).isEqualTo(jwtTokenResponse.getGrantType());
        assertThat(loginTokenResponse.getAccessToken()).isEqualTo(jwtTokenResponse.getAccessToken());
    }

    @Test
    @DisplayName("통합 로그아웃")
    public void 로그아웃() throws Exception {

        // given
        doReturn(resolvedAccessToken).when(jwtTokenProvider).resolveToken(accessToken);
        doReturn(1L).when(jwtTokenProvider).parseToken(resolvedAccessToken);
        doReturn(logoutAccessToken.getExpiration()).when(jwtTokenProvider).getRemainTime(resolvedAccessToken);

        // when
        Long memberId = memberService.expireToken(accessToken).getMemberId();

        // then
        assertThat(memberId).isEqualTo(jwtTokenProvider.parseToken(resolvedAccessToken));
    }

    @Test
    @DisplayName("토큰 재발급")
    @WithMockUser
    public void 토큰_재발급() throws Exception {

        // given
        doReturn(defaultMember).when(tokenService).getMemberFromAccessToken(accessToken);
        doReturn(refreshToken).when(refreshTokenService).findByMemberId(defaultMember.getId());
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateToken(defaultMember);

        // when
        JwtTokenResponse response = memberService.reissueToken(accessToken, refreshToken.getToken());

        // then
        assertThat(response).isEqualTo(jwtTokenResponse);
    }

    @Test
    @DisplayName("일반 회원 아이디 찾기")
    public void 일반_회원_아이디_찾기() throws Exception {

        // given
        String phoneNumber = defaultMember.getPhoneNumber();
        FindEmailRequest request = FindEmailRequest.builder()
                .phoneNumber(phoneNumber)
                .build();

        doReturn(Optional.of(defaultMember)).when(memberRepository).findByPhoneNumber(phoneNumber);

        // when
        FindEmailResponse response = memberService.findEmailFromPhoneNumber(request);

        // then
        assertThat(response.getEmail()).isEqualTo(defaultMember.getEmail());
    }

    @Test
    @DisplayName("소셜 회원 아이디 찾기는 불가능")
    public void 소셜_회원_아이디_찾기() throws Exception {

        // given
        String phoneNumber = socialMember.getPhoneNumber();
        FindEmailRequest request = FindEmailRequest.builder()
                .phoneNumber(phoneNumber)
                .build();

        doReturn(Optional.of(socialMember)).when(memberRepository).findByPhoneNumber(phoneNumber);

        // when
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> {
            memberService.findEmailFromPhoneNumber(request);
        });

        // then
        assertThat(badRequestException.getErrorCode()).isEqualTo(ErrorCode.NOT_DEFAULT_TYPE_USER);
    }

    @Test
    @DisplayName("일반 회원 비밀번호 찾기(임시 비밀번호 발급)")
    public void 일반_회원_임시_비밀번호_발급() throws Exception {

        // given
        FindPasswordRequest request = FindPasswordRequest.builder()
                .email(defaultMember.getEmail())
                .phoneNumber(defaultMember.getPhoneNumber())
                .build();

        String encode = "encode";
        doReturn(List.of(defaultMember)).when(memberRepository).findByEmail(defaultMember.getEmail());
        doReturn(encode).when(passwordEncoder).encode(any());

        // when
        FindPasswordResponse response = memberService.issueTemporaryPassword(request);

        // then
        assertThat(encode).isEqualTo(defaultMember.getPassword());
    }

    @Test
    @DisplayName("소셜 회원은 비밀번호 찾기(임시 비밀번호 발급)는 불가능")
    public void 소셜_회원_임시_비밀번호_발급() throws Exception {

        // given
        FindPasswordRequest request = FindPasswordRequest.builder()
                .email(socialMember.getEmail())
                .phoneNumber(socialMember.getPhoneNumber())
                .build();

        doReturn(List.of(socialMember)).when(memberRepository).findByEmail(socialMember.getEmail());

        // when
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> {
            memberService.issueTemporaryPassword(request);
        });


        // then
        assertThat(badRequestException.getErrorCode()).isEqualTo(NOT_DEFAULT_TYPE_USER);
    }

    @Test
    @DisplayName("프로필 사진 수정")
    @WithMockUser
    public void 프로필_사진_수정() throws Exception {

        // given
        String photo = "사진 경로";
        MultipartFile multipartFile = new MockMultipartFile("사진", new byte[2]);
        doReturn(defaultMember).when(tokenService).getMemberFromAccessToken(accessToken);
        doReturn(photo).when(storageService).uploadFile(multipartFile);

        // when
        memberService.addPhoto(accessToken, multipartFile);

        // then
        assertThat(defaultMember.getPhoto()).isEqualTo(photo);
    }

    @Test
    @DisplayName("닉네임 수정")
    @WithMockUser
    public void 닉네임_수정() throws Exception {

        // given
        PatchNicknameRequest request = PatchNicknameRequest
                .builder()
                .newNickname("newNickname")
                .build();

        doReturn(defaultMember).when(tokenService).getMemberFromAccessToken(accessToken);

        // when
        memberService.replaceNickname(accessToken, request);

        // then
        assertThat(defaultMember.getNickname()).isEqualTo(request.getNewNickname());
    }

    @Test
    @DisplayName("회원 탈퇴")
    @WithMockUser
    public void 회원_탈퇴() throws Exception {

        // given
        String password = defaultMember.getPassword();
        WithdrawRequest request = WithdrawRequest.builder()
                .password(password)
                .build();

        doReturn(defaultMember).when(tokenService).getMemberFromAccessToken(accessToken);
        doReturn(true).when(passwordEncoder).matches(password, defaultMember.getPassword());

        // when
        memberService.deactivate(accessToken, request);

        // then
        assertThat(defaultMember.isEnabled()).isFalse();
    }
}