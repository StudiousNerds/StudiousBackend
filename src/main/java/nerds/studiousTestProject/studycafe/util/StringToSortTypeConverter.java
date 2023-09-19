package nerds.studiousTestProject.studycafe.util;

import jakarta.annotation.Nullable;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchSortType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_SORT_TYPE;


/**
 * Controller 레이어에서 Request Param의 열거체 매핑을 해주는 메소드 (이상한 값 필터 후 기본값으로 설정)
 */
@Component
public class StringToSortTypeConverter implements Converter<String, SearchSortType> {
    @Override
    public SearchSortType convert(@Nullable String param) {
        System.out.println("param = " + param);
        if (param == null || param.isBlank()) {
            return SearchSortType.GRADE_DESC;
        }

        try {
            return SearchSortType.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(INVALID_SORT_TYPE);
        }
    }
}
