package nerds.studiousTestProject.bookmark.service;

import nerds.studiousTestProject.bookmark.dto.response.FindBookmarkResponse;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    @InjectMocks
    BookmarkService bookmarkService;

    @Mock
    BookmarkRepository bookmarkRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    StudycafeRepository studycafeRepository;

    @Mock
    ReservationRecordRepository reservationRecordRepository;

    @Mock
    HashtagRecordRepository hashtagRecordRepository;

    @Test
    @DisplayName("북마크를 등록할 수 있다.")
    void registerBookmark() {
        // given
        Studycafe studycafe = NERDS.생성();
        Member member = DEFAULT_USER.생성();

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(Optional.of(studycafe)).when(studycafeRepository).findById(studycafe.getId());

        // when
        bookmarkService.registerBookmark(member.getId(), studycafe.getId());

        //then
        Assertions.assertThat(member.getBookmarks().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("북마크를 삭제할 수 있다.")
    void deleteBookmark() {
        // given
        Studycafe studycafe = NERDS.생성();
        Member member = DEFAULT_USER.생성();
        Bookmark bookmark = Bookmark.builder().studycafe(studycafe).member(member).build();

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getId());
        doReturn(Optional.of(bookmark)).when(bookmarkRepository).findByStudycafeId(studycafe.getId());

        // when
        bookmarkService.deleteBookmark(member.getId(), studycafe.getId());

        // then
        Assertions.assertThat(member.getBookmarks()).isEmpty();
    }


    @Test
    @DisplayName("북마크를 조회할 수 있다.")
    void findBookmark() {
        // given
        Studycafe studycafe = NERDS.생성();
        Member member = DEFAULT_USER.생성();
        List<Bookmark> bookmarkList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            bookmarkList.add(Bookmark.builder().studycafe(studycafe).member(member).build());
        }
        Page<Bookmark> pages = new PageImpl<>(bookmarkList);

        doReturn(pages).when(bookmarkRepository).findAllByMemberId(member.getId(), null);

        // when
        FindBookmarkResponse bookmark = bookmarkService.findBookmark(member.getId(), null);

        // then
        Assertions.assertThat(bookmark.getBookmarkInfo().size()).isEqualTo(2);
    }
}