package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.BookmarkInfo;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.service.HashtagRecordService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
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

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final HashtagRecordService hashtagRecordService;
    private final MemberRepository memberRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final StudycafeService studycafeService;

    @Transactional
    public void registerBookmark(Long memberId, Long studycafeId){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);

        member.addBookmark(Bookmark.builder().studycafe(studyCafe).build());
    }

    public FindBookmarkResponse findBookmark(Long memberId, Pageable pageable){
        pageable = getPageable(pageable);
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
        Page<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(member.getId(), pageable);

        if (bookmarks == null || !bookmarks.hasContent()) {
            return FindBookmarkResponse.builder().totalPage(bookmarks.getTotalPages()).currentPage(bookmarks.getNumber() + 1)
                    .bookmarkInfo(Collections.emptyList()).build();
        }

        List<BookmarkInfo> bookmarkInfos = bookmarks.stream()
                .map(Bookmark::getStudycafe)
                .map(studycafe -> BookmarkInfo.builder()
                        .studycafeId(studycafe.getId())
                        .cafeName(studycafe.getName())
                        .photo(studycafe.getPhoto())
                        .accumRevCnt(findAllReservationRecordByStudycafeId(studycafe.getId()).size())
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(null)    // Studycafe 평점 컬럼 변경으로 인해 null로 수정 (기존 studycafe.getTotalGrade())
                        .hashtags(hashtagRecordService.findStudycafeHashtag(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());

        return FindBookmarkResponse.builder().totalPage(bookmarks.getTotalPages()).currentPage(bookmarks.getNumber() + 1)
                .bookmarkInfo(bookmarkInfos).build();
    }

    @Transactional
    public void deleteBookmark(Long memberId, Long studycafeId){
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
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

    private List<ReservationRecord> findAllReservationRecordByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }
}
