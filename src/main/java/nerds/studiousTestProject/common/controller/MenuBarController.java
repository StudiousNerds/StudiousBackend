package nerds.studiousTestProject.common.controller;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.member.dto.enquiry.response.MenuBarProfileResponse;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuBarController {
    private final MemberService memberService;

    @GetMapping("/top")
    public MenuBarProfileResponse topMenuBar(@LoggedInMember final Long memberId) {
        return memberService.getMenuBarProfile(memberId);
    }
}
