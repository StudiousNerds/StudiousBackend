package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.BookmarkReuqest;
import nerds.studiousTestProject.bookmark.dto.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.hashtag.service.HashtagRecordService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.service.MemberService;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_MEMBER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final MemberRepository memberRepository;
    private final StudycafeService studycafeService;
    private final HashtagRecordService hashtagRecordService;
    private final ReservationRecordService reservationRecordService;
    private final TokenService tokenService;

    @Transactional
    public void registerBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);

        member.addBookmark(Bookmark.builder().studycafe(studyCafe).build());

        return;
    }

    public List<FindBookmarkResponse> findBookmark(String accessToken, Integer pageNumber){
        List<FindBookmarkResponse> bookmarkCafeList = new ArrayList<>();
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Member bookmarkedMember = memberRepository.findById(member.getId()).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));
        List<Studycafe> bookmarkList = bookmarkedMember.getBookmarks().stream().map(b -> b.getStudycafe()).toList();

        // getBookmarkList(pageNumber, bookmarkCafeList, bookmarkList);
        return bookmarkCafeList;
    }

    @Transactional
    public void deleteBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);

        member.deleteBookmark(Bookmark.builder().studycafe(studyCafe).build());

        return;
    }

    private void getBookmarkList(Integer pageNumber, List<FindBookmarkResponse> bookmarkCafeList, List<Studycafe> bookmarkList) {
        for (Studycafe studycafe : bookmarkList) {
            FindBookmarkResponse bookmarkCafe = FindBookmarkResponse.builder()
                    .pageNumber(pageNumber)
                    .totalRecord(bookmarkList.size())
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(studycafe.getPhoto())
                    .accumRevCnt(reservationRecordService.findAllByStudycafeId(studycafe.getId()).size())
                    .distance(studycafe.getNearestStationInfo().getWalkingTime())
                    .nearestStation(studycafe.getNearestStationInfo().getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags((String[]) hashtagRecordService.findStudycafeHashtag(studycafe.getId()).toArray())
                    .build();
            bookmarkCafeList.add(bookmarkCafe);
        }
    }
}
