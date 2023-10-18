package nerds.studiousTestProject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.member.dto.inquire.response.MemberInfoResponse;
import nerds.studiousTestProject.member.dto.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.patch.PatchPasswordRequest;
import nerds.studiousTestProject.member.dto.patch.PatchPhoneNumberRequest;
import nerds.studiousTestProject.member.dto.withdraw.WithdrawRequest;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MyPageMemberController {
    private final MemberService memberService;

    @GetMapping
    public MemberInfoResponse inquireMember(@LoggedInMember Long memberId) {
        return memberService.findMemberInfoFromMemberId(memberId);
    }

    @PostMapping("/photo")
    public void addProfile(@LoggedInMember Long memberId, @RequestParam MultipartFile file) {
        memberService.addPhoto(memberId, file);
    }

    @PatchMapping("/nickname")
    public void patchNickname(@LoggedInMember Long memberId, @RequestBody PatchNicknameRequest patchNicknameRequest) {
        memberService.replaceNickname(memberId, patchNicknameRequest);
    }

    @PatchMapping("/phoneNumber")
    public void patchPhoneNumber(@LoggedInMember Long memberId, @RequestBody PatchPhoneNumberRequest patchPhoneNumberRequest) {
        memberService.replacePhoneNumber(memberId, patchPhoneNumberRequest);
    }

    @PatchMapping("/password")
    public void patchPassword(@LoggedInMember Long memberId, @RequestBody PatchPasswordRequest patchPasswordRequest) {
        memberService.replacePassword(memberId, patchPasswordRequest);
    }

    @PostMapping("/withdraw")
    public void withdraw(@LoggedInMember Long memberId, @RequestBody WithdrawRequest withdrawRequest) {
        memberService.deactivate(memberId, withdrawRequest);
    }
}
