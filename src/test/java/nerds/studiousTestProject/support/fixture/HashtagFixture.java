package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord.HashtagRecordBuilder;

public enum HashtagFixture {
    FIRST_HASHTAG(HashtagName.COST_EFFECTIVE),
    SECOND_HASHTAG(HashtagName.ACCESS);

    private HashtagName name;

    HashtagFixture(HashtagName name) {
        this.name = name;
    }

    public HashtagRecord 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public HashtagRecordBuilder 기본_정보_생성() {
        return HashtagRecord.builder()
                .name(this.name);
    }
}
