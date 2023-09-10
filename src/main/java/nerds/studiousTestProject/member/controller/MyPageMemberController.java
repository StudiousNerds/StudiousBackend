package nerds.studiousTestProject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.request.BookmarkRequest;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.member.dto.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.patch.PatchPasswordRequest;
import nerds.studiousTestProject.member.dto.withdraw.WithdrawRequest;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage/members")
public class MyPageMemberController {
    private final MemberService memberService;

    @PostMapping("/photo")
    @Secured(value = MemberRole.ROLES.USER)
    public void addProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam MultipartFile file) {
        log.info("file = {}", file);
        memberService.addPhoto(accessToken, file);
    }

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
