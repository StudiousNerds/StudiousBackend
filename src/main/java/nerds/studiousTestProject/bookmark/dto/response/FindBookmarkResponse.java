package nerds.studiousTestProject.bookmark.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FindBookmarkResponse {
    private PageInfo pageInfo;
    private List<BookmarkInfo> bookmarkInfo;
}
