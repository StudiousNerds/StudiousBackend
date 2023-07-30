package nerds.studiousTestProject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.member.dto.general.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.general.patch.PatchPasswordRequest;
import nerds.studiousTestProject.member.dto.general.withdraw.WithdrawRequest;
import nerds.studiousTestProject.member.service.member.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage/members")
public class MyPageMemberController {
    private final MemberService memberService;

    @PatchMapping("/nickname")
    public void patchNickname(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody PatchNicknameRequest patchNicknameRequest) {
        memberService.replaceNickname(accessToken, patchNicknameRequest);
    }

    @PatchMapping("/password")
    public void patchPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody PatchPasswordRequest patchPasswordRequest) {
        memberService.replacePassword(accessToken, patchPasswordRequest);
    }
    @PostMapping("/withdraw")
    public void withdraw(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody WithdrawRequest withdrawRequest) {
        memberService.deactivate(accessToken, withdrawRequest);
    }
}
