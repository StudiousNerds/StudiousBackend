package nerds.studiousTestProject.member.service.member;

import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.member.dto.find.FindEmailRequest;
import nerds.studiousTestProject.member.dto.find.FindEmailResponse;
import nerds.studiousTestProject.member.dto.find.FindPasswordRequest;
import nerds.studiousTestProject.member.dto.find.FindPasswordResponse;
import nerds.studiousTestProject.member.dto.inquire.response.MemberInfoResponse;
import nerds.studiousTestProject.member.dto.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.token.JwtTokenResponse;
import nerds.studiousTestProject.member.dto.withdraw.WithdrawRequest;
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

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_DEFAULT_TYPE_USER;
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
    StorageProvider storageProvider;

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
        doReturn(Optional.of(defaultMember)).when(memberRepository).findById(defaultMember.getId());
        doReturn(refreshToken).when(refreshTokenService).findByMemberId(defaultMember.getId());
        doReturn(jwtTokenResponse).when(jwtTokenProvider).generateToken(defaultMember);

        // when
        JwtTokenResponse response = memberService.reissueToken(defaultMember.getId(), refreshToken.getToken());

        // then
        assertThat(response).isEqualTo(jwtTokenResponse);
    }

    @Test
    @DisplayName("일반 회원 아이디 찾기")
    public void 일반_회원_아이디_찾기() throws Exception {

        // given
        String phoneNumber = defaultMember.getPhoneNumber();
        FindEmailRequest request = new FindEmailRequest();
        request.setPhoneNumber(phoneNumber);

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
        FindEmailRequest request = new FindEmailRequest();
        request.setPhoneNumber(phoneNumber);

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

        FindPasswordRequest request = new FindPasswordRequest();
        request.setEmail(defaultMember.getEmail());
        request.setPhoneNumber(defaultMember.getPhoneNumber());

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
        FindPasswordRequest request = new FindPasswordRequest();
        request.setEmail(socialMember.getEmail());
        request.setPhoneNumber(socialMember.getPhoneNumber());

        doReturn(List.of(socialMember)).when(memberRepository).findByEmail(socialMember.getEmail());

        // when
        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> {
            memberService.issueTemporaryPassword(request);
        });


        // then
        assertThat(badRequestException.getErrorCode()).isEqualTo(NOT_DEFAULT_TYPE_USER);
    }

    @Test
    @DisplayName("마이페이지 계정 관리")
    public void 계정_관리() throws Exception {

        // given
        doReturn(Optional.of(defaultMember)).when(memberRepository).findById(defaultMember.getId());

        // when
        MemberInfoResponse response = memberService.findMemberInfoFromMemberId(defaultMember.getId());

        // then
        assertThat(response.getName()).isEqualTo(defaultMember.getName());
        assertThat(response.getNickname()).isEqualTo(defaultMember.getNickname());
        assertThat(response.getEmail()).isEqualTo(defaultMember.getEmail());
        assertThat(response.getPhoto()).isEqualTo(defaultMember.getPhoto());
        assertThat(response.getPhoneNumber()).isEqualTo(defaultMember.getPhoneNumber());
    }

    @Test
    @DisplayName("프로필 사진 수정")
    public void 프로필_사진_수정() throws Exception {

        // given
        String photo = "사진 경로";
        MultipartFile multipartFile = new MockMultipartFile("사진", new byte[2]);

        doReturn(Optional.of(defaultMember)).when(memberRepository).findById(defaultMember.getId());
        doReturn(photo).when(storageProvider).uploadFile(multipartFile);

        // when
        memberService.addPhoto(defaultMember.getId(), multipartFile);

        // then
        assertThat(defaultMember.getPhoto()).isEqualTo(photo);
    }

    @Test
    @DisplayName("닉네임 수정")
    public void 닉네임_수정() throws Exception {

        // given
        PatchNicknameRequest request = new PatchNicknameRequest();
        request.setNewNickname("newNickname");

        doReturn(Optional.of(defaultMember)).when(memberRepository).findById(defaultMember.getId());

        // when
        memberService.replaceNickname(defaultMember.getId(), request);

        // then
        assertThat(defaultMember.getNickname()).isEqualTo(request.getNewNickname());
    }

    @Test
    @DisplayName("회원 탈퇴")
    public void 회원_탈퇴() throws Exception {

        // given
        String password = defaultMember.getPassword();
        WithdrawRequest request = WithdrawRequest.builder()
                .password(password)
                .build();

        doReturn(Optional.of(defaultMember)).when(memberRepository).findById(defaultMember.getId());
        doReturn(true).when(passwordEncoder).matches(password, defaultMember.getPassword());

        // when
        memberService.deactivate(defaultMember.getId(), request);

        // then
        assertThat(defaultMember.isEnabled()).isFalse();
    }
}