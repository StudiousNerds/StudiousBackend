package nerds.studiousTestProject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.member.dto.enquiry.response.ProfileResponse;
import nerds.studiousTestProject.member.dto.modify.request.ModifyNicknameRequest;
import nerds.studiousTestProject.member.dto.modify.request.ModifyPasswordRequest;
import nerds.studiousTestProject.member.dto.modify.request.ModifyPhoneNumberRequest;
import nerds.studiousTestProject.member.dto.modify.response.ModifyNicknameResponse;
import nerds.studiousTestProject.member.dto.modify.response.ModifyPhotoResponse;
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
    public ProfileResponse enquiryProfile(@LoggedInMember Long memberId) {
        return memberService.getProfile(memberId);
    }

    @PostMapping("/photo")
    public ModifyPhotoResponse modifyPhoto(@LoggedInMember Long memberId, @RequestParam MultipartFile file) {
        return memberService.modifyPhoto(memberId, file);
    }

    @PatchMapping("/nickname")
    public ModifyNicknameResponse modifyNickname(@LoggedInMember Long memberId, @RequestBody ModifyNicknameRequest modifyNicknameRequest) {
        return memberService.modifyNickname(memberId, modifyNicknameRequest);
    }

    @PatchMapping("/phoneNumber")
    public void modifyPhoneNumber(@LoggedInMember Long memberId, @RequestBody ModifyPhoneNumberRequest modifyPhoneNumberRequest) {
        memberService.modifyPhoneNumber(memberId, modifyPhoneNumberRequest);
    }

    @PatchMapping("/password")
    public void modifyPassword(@LoggedInMember Long memberId, @RequestBody ModifyPasswordRequest modifyPasswordRequest) {
        memberService.modifyPassword(memberId, modifyPasswordRequest);
    }

    @PostMapping("/withdraw")
    public void withdraw(@LoggedInMember Long memberId, @RequestBody WithdrawRequest withdrawRequest) {
        memberService.deactivate(memberId, withdrawRequest);
    }
}
