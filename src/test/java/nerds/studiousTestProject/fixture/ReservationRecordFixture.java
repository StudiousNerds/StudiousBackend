package nerds.studiousTestProject.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord.ReservationRecordBuilder;
import nerds.studiousTestProject.room.entity.Room;

public enum ReservationRecordFixture {

    FIRST_RESERVATION("최감자", "01023456789"),
    SECOND_RESERVATION("최잡종", "01082825918"),
    THIRD_RESERVATION("최보살", "01010041818");

    private final String name;
    private final String phoneNumber;

    ReservationRecordFixture(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public ReservationRecord 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public ReservationRecordBuilder 기본_정보_생성() {
        return ReservationRecord.builder()
                .name(this.name)
                .phoneNumber(this.phoneNumber);
    }

    public ReservationRecord 룸_생성(Room room, Long id) {
        return 기본_정보_생성()
                .id(id)
                .room(room)
                .build();
    }

    public ReservationRecord 멤버_생성(Member member, Long id) {
        return 기본_정보_생성()
                .id(id)
                .member(member)
                .build();
    }
}
