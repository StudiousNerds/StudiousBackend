package nerds.studiousTestProject.bookmark.repository;

import nerds.studiousTestProject.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findAllByMemberId(Long memberId, Pageable pageable);
    Optional<Bookmark> findByStudycafeId(Long studycafeId);
}
