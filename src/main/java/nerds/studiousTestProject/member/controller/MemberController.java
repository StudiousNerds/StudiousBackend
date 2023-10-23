package nerds.studiousTestProject.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.member.dto.find.request.FindEmailRequest;
import nerds.studiousTestProject.member.dto.find.response.FindEmailResponse;
import nerds.studiousTestProject.member.dto.find.request.FindPasswordRequest;
import nerds.studiousTestProject.member.dto.find.response.FindPasswordResponse;
import nerds.studiousTestProject.member.dto.login.request.LoginRequest;
import nerds.studiousTestProject.member.dto.login.response.LoginResponse;
import nerds.studiousTestProject.member.dto.logout.LogoutResponse;
import nerds.studiousTestProject.member.dto.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.token.JwtTokenResponse;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    private static final String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/signup")
    public JwtTokenResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return memberService.register(signUpRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return memberService.issueToken(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/email")
    public FindEmailResponse findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        return memberService.enquiryEmail(findEmailRequest);
    }

    @PostMapping("/password")
    public FindPasswordResponse findPassword(@RequestBody @Valid FindPasswordRequest findPasswordRequest) {
        return memberService.issueTemporaryPassword(findPasswordRequest);
    }

    @PostMapping("/logout")
    public LogoutResponse logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return memberService.expireToken(accessToken);
    }

    @PostMapping("/reissue")
    public LoginResponse reissue(@LoggedInMember Long memberId, @CookieValue(REFRESH_TOKEN) String refreshToken) {
        return memberService.reissueToken(memberId, refreshToken);
    }
}
