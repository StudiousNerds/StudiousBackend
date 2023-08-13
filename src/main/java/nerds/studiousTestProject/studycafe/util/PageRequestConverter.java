package nerds.studiousTestProject.studycafe.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 기존 PageRequest에서 다음 기능을 개선
 * - page값이 잘못 된 경우 0
 * - size값 8로 고정
 */
public class PageRequestConverter {
    public static Pageable of(Integer page, int size) {
        if (page == null || page < 1) {
            page = 1;
        }

        return PageRequest.of(page - 1, size);
    }
}
