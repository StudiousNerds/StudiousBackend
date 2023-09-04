package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.convenience.entity.ConvenienceType;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

public enum ConvenienceFixture {
    STUDYCAFE_ELEVATOR_FREE(ConvenienceName.ELEVATOR, 0),
    ROOM_HDMI_FREE(ConvenienceName.HDMI, 0);

    private final ConvenienceName name;
    private final int price;

    ConvenienceFixture(ConvenienceName name, int price) {
        this.name = name;
        this.price = price;
    }

    public Convenience 생성() {
        return 생성(null);
    }

    public Convenience 생성(Long id) {
        return 기본_정보_빌더_생성(id).build();
    }

    public Convenience.ConvenienceBuilder 기본_정보_빌더_생성(Long id) {
        return Convenience.builder()
                .id(id)
                .name(this.name)
                .price(this.price)
                .isFree(this.price == 0);
    }

    public Convenience 스터디카페_생성(Studycafe studycafe, Long id) {
        return 기본_정보_빌더_생성(id)
                .studycafe(studycafe)
                .room(null)
                .type(ConvenienceType.STUDYCAFE)
                .build();
    }

    public Convenience 룸_생성(Room room, Long id) {
        return 기본_정보_빌더_생성(id)
                .room(room)
                .studycafe(null)
                .type(ConvenienceType.ROOM)
                .build();
    }
}
