package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.repository.StudycafeDslRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyCafeService {
    private final StudycafeDslRepository studycafeDslRepository;

    /**
     * 사용자가 정한 필터 및 정렬 조건을 반영하여 알맞는 카페 정보들을 반환하는 메소드
     * 해당 메소드에서 추가적으로 잘못 입력된 값에 대한 예외처리를 진행
     * @param searchRequest 사용자 검색 요청값
     * @param pageable 페이지
     * @return 검색 결과
     */
    @Transactional(readOnly = true)
    public List<SearchResponse> inquire(SearchRequest searchRequest, Pageable pageable) {

        // 날짜 선택이 안되었는데 시간을 선택한 경우
        if (searchRequest.getDate() == null && (searchRequest.getStartTime() != null || searchRequest.getEndTime() != null)) {
            throw new BadRequestException(ErrorCode.NOT_FOUND_DATE);
        }

        // 시작 시간이 끝 시간보다 이후인 경우
        if (searchRequest.getStartTime() != null && searchRequest.getEndTime() != null &&
        !searchRequest.getStartTime().isBefore(searchRequest.getEndTime())) {
            throw new BadRequestException(ErrorCode.START_TIME_AFTER_THAN_END_TIME);
        }

        return studycafeDslRepository.searchAll(searchRequest, pageable).getContent();
    }
}
