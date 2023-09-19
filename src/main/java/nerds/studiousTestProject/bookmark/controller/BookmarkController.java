package nerds.studiousTestProject.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import nerds.studiousTestProject.common.util.LoggedInMember;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{studycafeId}")
    public ResponseEntity<?> registerBookmark(@LoggedInMember Long memberId, @PathVariable Long studycafeId){
        bookmarkService.registerBookmark(memberId, studycafeId);
        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    @GetMapping
    public FindBookmarkResponse findBookmark(@LoggedInMember Long memberId, Pageable pageable){
        return bookmarkService.findBookmark(memberId, pageable);
    }

    @DeleteMapping("/{studycafeId}")
    public ResponseEntity<?> deleteBookmark(@LoggedInMember Long memberId, @PathVariable Long studycafeId){
        bookmarkService.deleteBookmark(memberId, studycafeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }
}
