package nerds.studiousTestProject.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.member.dto.find.FindEmailRequest;
import nerds.studiousTestProject.member.dto.find.FindEmailResponse;
import nerds.studiousTestProject.member.dto.find.FindPasswordRequest;
import nerds.studiousTestProject.member.dto.find.FindPasswordResponse;
import nerds.studiousTestProject.member.dto.login.LoginRequest;
import nerds.studiousTestProject.member.dto.logout.LogoutResponse;
import nerds.studiousTestProject.member.dto.signup.SignUpRequest;
import nerds.studiousTestProject.member.dto.token.JwtTokenResponse;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public JwtTokenResponse signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return memberService.register(signUpRequest);
    }

    @PostMapping("/login")
    public JwtTokenResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return memberService.issueToken(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/email")
    public FindEmailResponse findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        return memberService.findEmailFromPhoneNumber(findEmailRequest);
    }

    @PostMapping("/password")
    public FindPasswordResponse findPassword(@RequestBody @Valid FindPasswordRequest findPasswordRequest) {
        log.info("email = {}", findPasswordRequest.getEmail());
        log.info("phoneNumber = {}", findPasswordRequest.getPhoneNumber());
        return memberService.issueTemporaryPassword(findPasswordRequest);
    }

    @PostMapping("/logout")
    public LogoutResponse logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return memberService.expireToken(accessToken);
    }

    @PostMapping("/reissue")
    public JwtTokenResponse reissue(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @CookieValue("refresh_token") String refreshToken) {
        return memberService.reissueToken(accessToken, refreshToken);
    }

    /**
     * USER 권한에서 잘 실행되는지 테스트하기 위한 메소드
     * @return
     */
    @ResponseBody
    @GetMapping("/test")
    public String test() {
        return "success";
    }
}
