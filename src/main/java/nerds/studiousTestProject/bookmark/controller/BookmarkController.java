package nerds.studiousTestProject.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.request.BookmarkRequest;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious/mypage/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<?> registerBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkRequest bookmarkRequest){
        bookmarkService.registerBookmark(accessToken, bookmarkRequest);
        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    @GetMapping
    public FindBookmarkResponse findBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, Pageable pageable){
        return bookmarkService.findBookmark(accessToken, pageable);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteBookmark(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody BookmarkRequest bookmarkRequest){
        bookmarkService.deleteBookmark(accessToken, bookmarkRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }
}
