package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchInResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudycafeRepositoryCustom {
    Page<SearchInResponse> getSearchResult(final SearchRequest searchRequest, final Pageable pageable);
}
