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
@RequestMapping("/studious/mypage")
public class MyPageMemberController {
    private final MemberService memberService;
    private final BookmarkService bookmarkService;

    @PostMapping("/members/photo")
    @Secured(value = MemberRole.ROLES.USER)
    public void addProfile(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam MultipartFile file) {
        log.info("file = {}", file);
        memberService.addPhoto(accessToken, file);
    }

    @PatchMapping("/members/nickname")
    public void patchNickname(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody PatchNicknameRequest patchNicknameRequest) {
        memberService.replaceNickname(accessToken, patchNicknameRequest);
    }

    @PatchMapping("/members/password")
    public void patchPassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody PatchPasswordRequest patchPasswordRequest) {
        memberService.replacePassword(accessToken, patchPasswordRequest);
    }
    @PostMapping("/members/withdraw")
    public void withdraw(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody WithdrawRequest withdrawRequest) {
        memberService.deactivate(accessToken, withdrawRequest);
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<?> registerBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkRequest bookmarkRequest){
        bookmarkService.registerBookmark(accessToken, bookmarkRequest);
        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    @GetMapping("/bookmarks")
    public FindBookmarkResponse findBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, Pageable pageable){
        return bookmarkService.findBookmark(accessToken, pageable);
    }

    @DeleteMapping("/bookmarks")
    public ResponseEntity<?> deleteBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkRequest bookmarkRequest){
        bookmarkService.deleteBookmark(accessToken, bookmarkRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }
}
