package nerds.studiousTestProject.bookmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.bookmark.dto.response.BookmarkResponse;
import nerds.studiousTestProject.bookmark.dto.response.ShowBookmarkResponse;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.ALREADY_EXIST_BOOKMARK;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_BOOKMARK;
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
    public void registerBookmark(final Long memberId, final Long studycafeId){
        validateExistence(studycafeId);
        final Member member = findMemberById(memberId);
        final Studycafe studyCafe = findStudycafeById(studycafeId);
        member.addBookmark(Bookmark.builder().studycafe(studyCafe).build());
    }

    public ShowBookmarkResponse findBookmark(final Long memberId, final Pageable pageable){
        final Page<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(memberId, pageable);

        if (bookmarks == null || !bookmarks.hasContent()) {
            return ShowBookmarkResponse.builder().totalPage(bookmarks.getTotalPages()).currentPage(bookmarks.getNumber() + 1)
                    .bookmarkResponse(Collections.emptyList()).build();
        }

        List<BookmarkResponse> bookmarkResponses = bookmarks.stream()
                .map(Bookmark::getStudycafe)
                .map(studycafe -> BookmarkResponse.builder()
                        .studycafeId(studycafe.getId())
                        .cafeName(studycafe.getName())
                        .photo(studycafe.getPhoto())
                        .accumResCnt(findReservationRecordsByStudycafeId(studycafe.getId()).size())
                        .walkingTime(studycafe.getWalkingTime())
                        .nearestStation(studycafe.getNearestStation())
                        .grade(findGradeByStudycafe(studycafe))
                        .hashtags(findHashtagById(studycafe.getId()))
                        .build())
                .collect(Collectors.toList());

        return ShowBookmarkResponse.builder().totalPage(bookmarks.getTotalPages()).currentPage(bookmarks.getNumber() + 1)
                .bookmarkResponse(bookmarkResponses).build();
    }

    @Transactional
    public void deleteBookmark(final Long memberId, final Long studycafeId){
        final Member member = findMemberById(memberId);
        final Bookmark bookmark = findBookmarkById(studycafeId);
        member.deleteBookmark(bookmark);
        bookmarkRepository.deleteById(bookmark.getId());
    }

    private Bookmark findBookmarkById(Long studycafeId) {
        return bookmarkRepository.findByStudycafeId(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOKMARK));
    }

    private List<ReservationRecord> findReservationRecordsByStudycafeId(final Long studycafeId) {
        return reservationRecordRepository.findAllByStudycafeId(studycafeId);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    private Studycafe findStudycafeById(final Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private List<String> findHashtagById(final Long studycafeId) {
        final List<HashtagName> hashtagNames = hashtagRecordRepository.findHashtagRecordByStudycafeId(studycafeId);
        final int size = Math.min(hashtagNames.size(), TOTAL_HASHTAGS_COUNT);

        return hashtagNames.stream().map(tag -> tag.name()).limit(size).toList();
    }

    private double findGradeByStudycafe(final Studycafe studycafe) {
        return studycafe.getGradeSum() / studycafe.getGradeCount();
    }

    private void validateExistence(final Long studycafeId) {
        if(bookmarkRepository.existsByStudycafeId(studycafeId)) {
            throw new BadRequestException(ALREADY_EXIST_BOOKMARK);
        }
    }
}
