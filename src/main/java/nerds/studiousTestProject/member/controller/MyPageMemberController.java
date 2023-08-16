package nerds.studiousTestProject.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.BookmarkReuqest;
import nerds.studiousTestProject.bookmark.dto.FindBookmarkResponse;
import nerds.studiousTestProject.member.dto.general.patch.PatchNicknameRequest;
import nerds.studiousTestProject.member.dto.general.patch.PatchPasswordRequest;
import nerds.studiousTestProject.member.dto.general.withdraw.WithdrawRequest;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import nerds.studiousTestProject.member.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage")
public class MyPageMemberController {
    private final MemberService memberService;
    private final BookmarkService bookmarkService;

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

    @PostMapping("/bookmarks/{pageNumber}")
    public ResponseEntity<?> registerBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkReuqest bookmarkReuqest, @PathVariable("pageNumber") Integer pageNumber){
        bookmarkService.registerBookmark(accessToken, bookmarkReuqest);
        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    @GetMapping("/bookmarks/{pageNumber}")
    public List<FindBookmarkResponse> findBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable("pageNumber") Integer pageNumber){
        return bookmarkService.findBookmark(accessToken, pageNumber);
    }

    @DeleteMapping("/bookmarks/{pageNumber}")
    public ResponseEntity<?> deleteBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkReuqest bookmarkReuqest, @PathVariable("pageNumber") Integer pageNumber){
        bookmarkService.deleteBookmark(accessToken, bookmarkReuqest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }
}
