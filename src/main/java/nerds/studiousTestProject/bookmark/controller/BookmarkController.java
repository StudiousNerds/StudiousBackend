package nerds.studiousTestProject.bookmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.ShowBookmarkResponse;
import nerds.studiousTestProject.bookmark.service.BookmarkService;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private static final String STUDYCAFE_SEARCH_SIZE = "8";

    @PostMapping("/{studycafeId}")
    public ResponseEntity<Void> registerBookmark(@LoggedInMember Long memberId, @PathVariable Long studycafeId){
        bookmarkService.registerBookmark(memberId, studycafeId);
        return ResponseEntity.created(URI.create("/api/v1/bookmarks")).build();
    }

    @GetMapping
    public ShowBookmarkResponse findBookmark(@LoggedInMember Long memberId,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(defaultValue = STUDYCAFE_SEARCH_SIZE) Integer size){
        return bookmarkService.findBookmark(memberId, PageRequestConverter.of(page, size));
    }

    @DeleteMapping("/{studycafeId}")
    public ResponseEntity<Void> deleteBookmark(@LoggedInMember Long memberId, @PathVariable Long studycafeId){
        bookmarkService.deleteBookmark(memberId, studycafeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
