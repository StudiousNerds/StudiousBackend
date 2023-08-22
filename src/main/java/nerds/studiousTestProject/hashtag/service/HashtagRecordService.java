package nerds.studiousTestProject.hashtag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.HashtagRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class HashtagRecordService {
    private final HashtagRecordRepository hashtagRecordRepository;
    private final Integer TOTAL_HASHTAGS_COUNT = 5;

    public List<HashtagName> findStudycafeHashtag(Long studycafeId) {
//        List<HashtagName> hashtagNames = hashtagRecordRepository.findHashtagRecordByStudycafeId(studycafeId);
//        List<HashtagName> hashtagNameList = new ArrayList<>();
//        for (int i = 0; i < TOTAL_HASHTAGS_COUNT; i++) {
//            hashtagNameList.add(hashtagNames.get(i));
//        }
//        return hashtagNameList;
        return null;
    }
}
