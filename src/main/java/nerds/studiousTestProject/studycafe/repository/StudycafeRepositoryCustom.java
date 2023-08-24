package nerds.studiousTestProject.studycafe.repository;

import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudycafeRepositoryCustom {
    Page<Studycafe> getSearchResult(SearchRequest searchRequest, Pageable pageable);
}
