package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.BookmarkInfo;
import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final HashtagRecordRepository hashtagRecordRepository;
    private final MemberRepository memberRepository;
    private final ReservationRecordRepository reservationRecordRepository;
    private final StudycafeRepository studycafeRepository;
    private final Integer TOTAL_HASHTAGS_COUNT = 5;

    @Transactional
    public void registerBookmark(Long memberId, Long studycafeId){
        Member member = findMemberById(memberId);
        Studycafe studyCafe = findStudycafeById(studycafeId);
        member.addBookmark(Bookmark.builder().studycafe(studyCafe).build());
    }

    public FindBookmarkResponse findBookmark(Long memberId, Pageable pageable){
        pageable = getPageable(pageable);
        Member member = findMemberById(memberId);
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
                        .accumRevCnt(findReservationRecordsByStudycafeId(studycafe.getId()).size())
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(studycafe.getTotalGrade())
                        .hashtags(findHashtagById(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());

        return FindBookmarkResponse.builder().totalPage(bookmarks.getTotalPages()).currentPage(bookmarks.getNumber() + 1)
                .bookmarkInfo(bookmarkInfos).build();
    }

    @Transactional
    public void deleteBookmark(Long memberId, Long studycafeId){
        Member member = findMemberById(memberId);
        Studycafe studyCafe = findStudycafeById(studycafeId);

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

    private List<ReservationRecord> findReservationRecordsByStudycafeId(Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    private Studycafe findStudycafeById(Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private List<String> findHashtagById(Long studycafeId) {
        List<HashtagName> hashtagNames = hashtagRecordRepository.findHashtagRecordByStudycafeId(studycafeId);

        int size = Math.min(hashtagNames.size(), TOTAL_HASHTAGS_COUNT);

        List<String> hashtagNameList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hashtagNameList.add(hashtagNames.get(i).name());
        }
        return hashtagNameList;
    }
}
