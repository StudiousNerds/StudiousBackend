package nerds.studiousTestProject.bookmark.dto.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;

@Builder
@Data
public class PageInfo {
    private Integer currentPage;
    private Integer totalPage;

    public static PageInfo of(Page<Bookmark> bookmarks) {
        return PageInfo.builder()
                .currentPage(bookmarks.getNumber())
                .totalPage(bookmarks.getTotalPages())
                .build();
    }
}
