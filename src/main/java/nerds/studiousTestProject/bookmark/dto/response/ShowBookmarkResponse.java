package nerds.studiousTestProject.bookmark.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ShowBookmarkResponse {
    private Integer totalPage;
    private Integer currentPage;
    private List<BookmarkInfo> bookmarkInfo;
}
