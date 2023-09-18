package nerds.studiousTestProject.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import nerds.studiousTestProject.common.util.RoleType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Secured(value = {RoleType.USER, RoleType.ADMIN, RoleType.SUPER_ADMIN})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{studycafeId}")
    public ResponseEntity<?> registerBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId){
        bookmarkService.registerBookmark(accessToken, studycafeId);
        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    @GetMapping
    public FindBookmarkResponse findBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, Pageable pageable){
        return bookmarkService.findBookmark(accessToken, pageable);
    }

    @DeleteMapping("/{studycafeId}")
    public ResponseEntity<?> deleteBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId){
        bookmarkService.deleteBookmark(accessToken, studycafeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }
}
