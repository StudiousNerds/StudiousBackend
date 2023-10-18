package nerds.studiousTestProject.bookmark.service;

import nerds.studiousTestProject.bookmark.entity.Bookmark;
import nerds.studiousTestProject.bookmark.repository.BookmarkRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static nerds.studiousTestProject.support.fixture.MemberFixture.DEFAULT_USER;
import static nerds.studiousTestProject.support.fixture.StudycafeFixture.NERDS;
import static org.junit.jupiter.api.Assertions.*;
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
}