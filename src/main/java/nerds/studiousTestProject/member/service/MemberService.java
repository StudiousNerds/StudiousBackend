package nerds.studiousTestProject.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageService;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.member.dto.find.FindEmailRequest;
import nerds.studiousTestProject.member.dto.find.FindEmailResponse;
import nerds.studiousTestProject.member.dto.find.FindPasswordRequest;
import nerds.studiousTestProject.member.dto.find.FindPasswordResponse;
import nerds.studiousTestProject.member.dto.inquire.response.MemberInfoResponse;
import nerds.studiousTestProject.member.dto.logout.LogoutResponse;
import nerds.studiousTestProject.member.dto.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.patch.PatchPasswordRequest;
import nerds.studiousTestProject.member.dto.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.token.JwtTokenResponse;
import nerds.studiousTestProject.member.dto.withdraw.WithdrawRequest;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.entity.token.LogoutAccessToken;
import nerds.studiousTestProject.member.entity.token.RefreshToken;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.service.token.LogoutAccessTokenService;
import nerds.studiousTestProject.member.service.token.RefreshTokenService;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static nerds.studiousTestProject.common.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static nerds.studiousTestProject.common.exception.ErrorCode.ALREADY_EXIST_PHONE_NUMBER;
import static nerds.studiousTestProject.common.exception.ErrorCode.ALREADY_EXIST_USER;
import static nerds.studiousTestProject.common.exception.ErrorCode.EXPIRED_TOKEN_VALID_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.EXPIRE_USER;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_EMAIL;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_PASSWORD;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_PHONE_NUMBER;
import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_TOKEN;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_DEFAULT_TYPE_USER;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;
    private final LogoutAccessTokenService logoutAccessTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final StorageService storageService;

    /**
     * 사용자가 입력한 정보를 가지고 MemberRepository에 저장하는 메소드
     * @param signUpRequest 회원 가입 폼에서 입력한 정보
     * 이 때, MemberType은 프론트에서 이전에 백으로 부터 전달받은 값 (없다면 null)
     * @return 회원가입한 정보로 만든 토큰 값
     */
    @Transactional
    public JwtTokenResponse register(SignUpRequest signUpRequest) {
        validate(signUpRequest);

        String encodedPassword = getEncodedPassword(signUpRequest);
        Member member = signUpRequest.toEntity(encodedPassword);

        memberRepository.save(member);
        return jwtTokenProvider.generateToken(member);
    }

    /**
     * 로그인 하는 시점에 토큰을 생성해서 반환하는 메소드 (로그인을 하는 시점에 토큰이 생성된다)
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 발급한 토큰 정보
     */
    @Transactional
    public JwtTokenResponse issueToken(String email, String password) {
        List<Member> members = memberRepository.findByEmail(email);
        if (members.isEmpty()) {
            throw new NotFoundException(MISMATCH_EMAIL);
        }

        Member member = members.stream().filter(m -> passwordEncoder.matches(password, m.getPassword())).findAny().orElseThrow(() -> new NotFoundException(MISMATCH_PASSWORD));
        if (!member.getType().equals(MemberType.DEFAULT)) {
            throw new NotFoundException(NOT_DEFAULT_TYPE_USER);
        }

        if (!member.isEnabled()) {
            throw new NotFoundException(EXPIRE_USER);
        }

        return jwtTokenProvider.generateToken(member);
    }

    /**
     * 현재 사용자의 토큰을 만료시고 블랙리스트에 저장하는 메소드
     * @param accessToken 사용자의 accessToken
     * @return 현재 사용자의 PK
     */
    @Transactional
    public LogoutResponse expireToken(String accessToken) {
        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        Long memberId = jwtTokenProvider.parseToken(resolvedAccessToken);
        Long remainTime = jwtTokenProvider.getRemainTime(resolvedAccessToken);

        refreshTokenService.deleteByMemberId(memberId);
        logoutAccessTokenService.saveLogoutAccessToken(LogoutAccessToken.from(resolvedAccessToken, remainTime));

        // LogoutDB 가 과부화될 가능성 있음 => 토큰 유효기간이 만료되면 자동 삭제되므로 염려할 필요 X
        return LogoutResponse.builder()
                .memberId(memberId)
                .build();
    }

    public MemberInfoResponse findMemberInfoFromAccessToken(String accessToken) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        return MemberInfoResponse.of(member);
    }

    @Transactional
    public void addPhoto(String accessToken, MultipartFile file) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        String photoUrl = storageService.uploadFile(file);
        member.updatePhoto(photoUrl);
    }

    @Transactional
    public FindEmailResponse findEmailFromPhoneNumber(FindEmailRequest findEmailRequest) {
        String phoneNumber = findEmailRequest.getPhoneNumber();

        Member member = memberRepository.findByPhoneNumber(phoneNumber).
                orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));

        if (!member.getType().equals(MemberType.DEFAULT)) {
            throw new BadRequestException(NOT_DEFAULT_TYPE_USER);
        }

        return FindEmailResponse.builder()
                .email(member.getEmail())
                .build();
    }

    /**
     * 이메일과 전화번호를 통해 알맞는 회원의 비밀번호를 임시 비밀번호로 수정 및 임시 비밀번호를 반환하는 메소드
     * @param findPasswordRequest 이메일, 비밀번호
     * @return 발급된 임시 비밀번호
     */
    @Transactional
    public FindPasswordResponse issueTemporaryPassword(FindPasswordRequest findPasswordRequest) {
        String email = findPasswordRequest.getEmail();
        String phoneNumber = findPasswordRequest.getPhoneNumber();

        List<Member> members = memberRepository.findByEmail(email);
        if (members.isEmpty()) {
            throw new NotFoundException(MISMATCH_EMAIL);
        }

        Member member = members.stream().filter(m -> m.getPhoneNumber().equals(phoneNumber)).findAny()
                .orElseThrow(() -> new NotFoundException(MISMATCH_PHONE_NUMBER));
        if (!member.getType().equals(MemberType.DEFAULT)) {
            throw new BadRequestException(NOT_DEFAULT_TYPE_USER);
        }

        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        String encode = passwordEncoder.encode(temporaryPassword);
        member.updatePassword(encode);

        return FindPasswordResponse.builder()
                .tempPassword(temporaryPassword)
                .build();
    }

    @Transactional
    public void replacePassword(String accessToken, PatchPasswordRequest patchPasswordRequest) {
        String oldPassword = patchPasswordRequest.getOldPassword();
        String newPassword = patchPasswordRequest.getNewPassword();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new BadRequestException(MISMATCH_PASSWORD);
        }

        // 회원 비밀번호 수정
        String encode = passwordEncoder.encode(newPassword);
        member.updatePassword(encode);
    }

    @Transactional
    public void replaceNickname(String accessToken, PatchNicknameRequest patchNicknameRequest) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        String newNickname = patchNicknameRequest.getNewNickname();

        if (memberRepository.existsByPhoneNumber(newNickname)) {
            throw new BadRequestException(ALREADY_EXIST_NICKNAME);
        }

        member.updateNickname(newNickname);
    }

    @Transactional
    public void deactivate(String accessToken, WithdrawRequest withdrawRequest) {
        String password = withdrawRequest.getPassword();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BadRequestException(MISMATCH_PASSWORD);
        }

        member.withdraw();
        expireToken(accessToken);
    }

    /**
     * 사용자가 만료된 accessToken 과 만료되지 않은 refreshToken을 넘길 때 새로운 accessToken을 만들어 주는 메소드
     * RefreshToken의 유효기간을 확인 후, 토큰을 재발급해주는 메소드
     *
     * @param accessToken
     * @param refreshToken 사용자로부터 넘겨 받은 refreshToken
     * @return 새로운 accessToken 이 담긴 JwtTokenResponse 객체
     */
    @Transactional
    public JwtTokenResponse reissueToken(String accessToken, String refreshToken) {
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        RefreshToken redisRefreshToken = refreshTokenService.findByMemberId(member.getId());
        if (redisRefreshToken == null) {
            throw new NotFoundException(EXPIRED_TOKEN_VALID_TIME);
        }

        if (!refreshToken.equals(redisRefreshToken.getToken())) {
            log.info("refreshToken = {}", refreshToken);
            log.info("redisRefreshToken = {}", redisRefreshToken.getToken());
            throw new NotFoundException(MISMATCH_TOKEN);
        }

//        Authorization 사용하여 패스워드 가져올 때 PROTECTED 되있으므로 DB에서 사용자 내역을 가져온다.
//        String password = userDetails.getPassword();
//        참고 : https://djunnni.gitbook.io/springboot/2019-11-30
//        Member member = memberRepository.findById(currentEmail).get();
//        String password = passwordEncoder.encode(member.getPassword());

        return jwtTokenProvider.generateToken(member);
    }

    private void validate(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmailAndType(signUpRequest.getEmail(), MemberType.DEFAULT)) {
            throw new BadRequestException(ALREADY_EXIST_USER);
        }

        if (memberRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new BadRequestException(ALREADY_EXIST_PHONE_NUMBER);
        }

        if (memberRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new BadRequestException(ALREADY_EXIST_NICKNAME);
        }
    }

    /**
     * 인코딩된 비밀번호를 발급해주는 메소드
     * (만약, 소셜 로그인인 경우 UUID를 통한 랜덤 문자열을 인코딩하여 반환)
     * @param signUpRequest 로그인 정보
     * @return 인코딩된 비밀번호
     */
    private String getEncodedPassword(SignUpRequest signUpRequest) {
        String password = signUpRequest.getPassword() == null ? UUID.randomUUID().toString() : signUpRequest.getPassword();
        return passwordEncoder.encode(password);
    }
}