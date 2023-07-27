package nerds.studiousTestProject.hashtag.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.hashtag.entity.Hashtag;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.repository.HashtagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    public String[] findHashtags(Long id){
        HashtagRecord hashtagRecord = hashtagRepository.findByStudycafeId(id)
                .orElseThrow(() -> new EntityNotFoundException("No such Studycafe's hashtag"));

        List<Hashtag> hashtagList = hashtagRecord.getHashtags();
        Integer arrSize = hashtagList.size();
        String hashtags[] = new String[arrSize];
        for (int i = 0; i < arrSize; i++) {
            hashtags[i] = hashtagList.get(i).toString();
        }

        return hashtags;
    }
}