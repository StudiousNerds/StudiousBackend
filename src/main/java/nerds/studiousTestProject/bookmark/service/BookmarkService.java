package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.BookmarkReuqest;
import nerds.studiousTestProject.bookmark.dto.FindBookmarkResponse;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.service.MemberService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final StudycafeService studycafeService;

    @Transactional
    public void registerBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = memberService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);
        member.registerBookmark(studyCafe.getId());

        return;
    }

    public List<FindBookmarkResponse> findBookmark(String accessToken, Integer pageNumber){
        List<FindBookmarkResponse> bookmarkCafeList = new ArrayList<>();
        Member member = memberService.getMemberFromAccessToken(accessToken);
        Member bookmarkedMember = memberRepository.findById(member.getId()).orElseThrow(() -> new NotFoundException(NOT_FOUND_MEMBER));
        List<String> bookmarkList = bookmarkedMember.getBookmarks().stream().map(b -> b.getStudycafe().getName()).toList();

        getBookmarkList(pageNumber, bookmarkCafeList, bookmarkList);
        return bookmarkCafeList;
    }

    @Transactional
    public void deleteBookmark(String accessToken, BookmarkReuqest bookmarkReuqest){
        Long studycafeId = bookmarkReuqest.getStudycafeId();

        Member member = memberService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);
        member.deleteBookmark(studyCafe.getId());

        return;
    }

    private void getBookmarkList(Integer pageNumber, List<FindBookmarkResponse> bookmarkCafeList, List<Long> bookmarkList) {
        for (Long studycafeId : bookmarkList) {
            Studycafe studycafe = studycafeService.getStudyCafe(studycafeId);
            FindBookmarkResponse bookmarkCafe = FindBookmarkResponse.builder()
                    .pageNumber(pageNumber)
                    .totalRecord(bookmarkList.size())
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(studycafe.getPhoto())
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getNearestStationInfo().getWalkingTime())
                    .nearestStation(studycafe.getNearestStationInfo().getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags((String[]) studycafe.getAccumHashtagHistories().toArray())
                    .build();
            bookmarkCafeList.add(bookmarkCafe);
        }
    }
}