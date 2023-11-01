package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponseInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudycafeRepositoryCustom {
    Page<SearchResponseInfo> getSearchResult(final SearchRequest searchRequest, final Pageable pageable);
}
