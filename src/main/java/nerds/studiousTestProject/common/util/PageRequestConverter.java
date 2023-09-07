package nerds.studiousTestProject.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 기존 PageRequest에서 다음 기능을 개선
 * - page값이 잘못 된 경우 0
 * - sort값이 null 인경우 Sort.unsorted() 로 핸들링
 */
public class PageRequestConverter {
    public static Pageable of(Integer page, int size) {
        if (page == null || page < 1) {
            page = 1;
        }

        return PageRequest.of(page - 1, size);
    }

    public static Pageable of(Integer page, int size, Sort sort) {
        if (page == null || page < 1) {
            page = 1;
        }

        if (sort == null) {
            sort = Sort.unsorted();
        }

        return PageRequest.of(page - 1, size, sort);
    }
}
