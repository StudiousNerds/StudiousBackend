package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.entity.Room.RoomBuilder;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

public enum RoomFixture {

    ROOM_FOUR_SIX(4,6);

    private final int minHeadCount;

    private final int maxHeadCount;

    RoomFixture(int minHeadCount, int maxHeadCount) {
        this.minHeadCount = minHeadCount;
        this.maxHeadCount = maxHeadCount;
    }


    public Room 생성(){
        return 기본_정보_생성().build();
    }

    public Room 생성(Long id){
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public RoomBuilder 기본_정보_생성() {
        return Room.builder()
                .minHeadCount(this.minHeadCount)
                .maxHeadCount(this.maxHeadCount);

    }

    public Room 스터디카페_생성(Studycafe studyCafe) {
        return 기본_정보_생성()
                .studycafe(studyCafe)
                .build();
    }

    public Room 스터디카페_생성(Studycafe studyCafe, Long id){
        return 기본_정보_생성()
                .id(id)
                .studycafe(studyCafe)
                .build();
    }

}
