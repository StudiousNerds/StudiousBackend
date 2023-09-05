package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.request.BookmarkRequest;
import nerds.studiousTestProject.bookmark.dto.response.BookmarkInfo;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.dto.response.PageInfo;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.hashtag.service.HashtagRecordService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final StudycafeService studycafeService;
    private final HashtagRecordService hashtagRecordService;
    private final ReservationRecordService reservationRecordService;
    private final TokenService tokenService;

    @Transactional
    public void registerBookmark(String accessToken, BookmarkRequest bookmarkRequest){
        Long studycafeId = bookmarkRequest.getStudycafeId();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);

        member.addBookmark(Bookmark.builder().studycafe(studyCafe).build());
    }

    public FindBookmarkResponse findBookmark(String accessToken, Pageable pageable){
        pageable = getPageable(pageable);
        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(member.getId(), pageable);

        if (bookmarks == null || !bookmarks.hasContent()) {
            return FindBookmarkResponse.builder().pageInfo(PageInfo.of(bookmarks)).bookmarkInfo(Collections.emptyList()).build();
        }

        List<BookmarkInfo> bookmarkInfos = bookmarks.stream()
                .map(Bookmark::getStudycafe)
                .map(studycafe -> BookmarkInfo.builder()
                        .studycafeId(studycafe.getId())
                        .cafeName(studycafe.getName())
                        .photo(studycafe.getPhoto())
                        .accumRevCnt(reservationRecordService.findAllByStudycafeId(studycafe.getId()).size())
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(studycafe.getTotalGrade())
                        .hashtags(hashtagRecordService.findStudycafeHashtag(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());

        return FindBookmarkResponse.builder().pageInfo(PageInfo.of(bookmarks)).bookmarkInfo(bookmarkInfos).build();
    }

    @Transactional
    public void deleteBookmark(String accessToken, BookmarkRequest bookmarkRequest){
        Long studycafeId = bookmarkRequest.getStudycafeId();

        Member member = tokenService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);

        member.deleteBookmark(Bookmark.builder().studycafe(studyCafe).build());
        bookmarkRepository.deleteById(studycafeId);
    }

    private PageRequest getPageable(Pageable pageable) {
        Integer page = Integer.valueOf(pageable.getPageNumber());
        if(page == null || page < 1) {
            return PageRequest.of(1, pageable.getPageSize(), pageable.getSort());
        }

        return PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
    }
}
