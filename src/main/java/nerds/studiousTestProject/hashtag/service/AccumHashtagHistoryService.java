package nerds.studiousTestProject.hashtag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.hashtag.entity.AccumHashtagHistory;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.repository.AccumHashtagHistoryRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class AccumHashtagHistoryService {
    private final AccumHashtagHistoryRepository accumHashtagHistoryRepository;

    public String[] getTop5AccumHashtagHistory(Studycafe studycafe) {
        List<AccumHashtagHistory> hashtagHistoryList = accumHashtagHistoryRepository.findTop5ByStudycafeId(studycafe.getId());
        List<HashtagName> hashtagList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            hashtagList.add(hashtagHistoryList.get(i).getName());
        }

        return (String[])hashtagList.toArray();
    }
}
