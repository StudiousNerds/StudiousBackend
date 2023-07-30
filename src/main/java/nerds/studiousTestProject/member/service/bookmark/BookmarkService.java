package nerds.studiousTestProject.member.service.bookmark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.service.HashtagService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.member.dto.bookmark.BookmarkReuqest;
import nerds.studiousTestProject.member.dto.bookmark.FindBookmarkResponse;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.member.MemberRepository;
import nerds.studiousTestProject.member.service.member.MemberService;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final StudycafeService studycafeService;
    private final SubPhotoService subPhotoService;
    private final HashtagService hashtagService;

    @Transactional
    public ResponseEntity<?> registerBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = memberService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);
        member.registerBookmark(studyCafe.getName());

        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }

    public List<FindBookmarkResponse> findBookmark(String accessToken, Integer pageNumber){
        List<FindBookmarkResponse> bookmarkCafeList = new ArrayList<>();
        Member member = memberService.getMemberFromAccessToken(accessToken);
        Member bookmarkedMember = memberRepository.findById(member.getId()).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));
        List<String> bookmarkList = bookmarkedMember.getBookmark();

        getBookmarkList(pageNumber, bookmarkCafeList, bookmarkList);
        return bookmarkCafeList;
    }

    @Transactional
    public ResponseEntity<?> deleteBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = memberService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);
        member.deleteBookmark(studyCafe.getName());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("북마크 삭제에 성공했습니다.");
    }

    private void getBookmarkList(Integer pageNumber, List<FindBookmarkResponse> bookmarkCafeList, List<String> bookmarkList) {
        for (String s : bookmarkList) {
            Studycafe studycafe = studycafeService.getStudyCafeByName(s);
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            FindBookmarkResponse bookmarkCafe = FindBookmarkResponse.builder()
                    .pageNumber(pageNumber)
                    .totalRecord(bookmarkList.size())
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .nearestStation(studycafe.getNearestStation())
                    .grade(studycafe.getTotalGarde())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            bookmarkCafeList.add(bookmarkCafe);
        }
    }
}