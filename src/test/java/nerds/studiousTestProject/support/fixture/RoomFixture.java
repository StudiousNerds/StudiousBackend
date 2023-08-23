package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.entity.Room.RoomBuilder;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import static nerds.studiousTestProject.room.entity.PriceType.*;

public enum RoomFixture {

    ROOM_TWO_FOUR("A룸", 2, 4, 1, PER_HOUR, 1000),
    ROOM_FOUR_SIX("B룸", 4,6, 1, PER_HOUR, 1000);

    private final String name;
    private final int minHeadCount;
    private final int maxHeadCount;
    private final int minUsingTime;
    private final PriceType type;
    private final int price;

    RoomFixture(String name, int minHeadCount, int maxHeadCount, int minUsingTime, PriceType type, int price) {
        this.name = name;
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
        this.minUsingTime = minUsingTime;
        this.type = type;
        this.price = price;
    }

    public Room 생성(){
        return 생성(null);
    }

    public Room 생성(Long id){
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public RoomBuilder 기본_정보_생성() {
        return Room.builder()
                .name(this.name)
                .minHeadCount(this.minHeadCount)
                .maxHeadCount(this.maxHeadCount)
                .minUsingTime(this.minUsingTime)
                .type(this.type)
                .price(this.price);
    }

    public Room 스터디카페_생성(Studycafe studyCafe) {
        return 스터디카페_생성(studyCafe, null);
    }

    public Room 스터디카페_생성(Studycafe studyCafe, Long id){
        return 기본_정보_생성()
                .id(id)
                .studycafe(studyCafe)
                .build();
    }
}
