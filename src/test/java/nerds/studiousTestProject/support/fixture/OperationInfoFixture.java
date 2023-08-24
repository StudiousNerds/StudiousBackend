package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;

import java.time.LocalTime;

import static nerds.studiousTestProject.studycafe.entity.Week.*;

public enum OperationInfoFixture {
    MON_NINE_TO_NINE(MONDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    TUE_NINE_TO_NINE(TUESDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    WED_NINE_TO_NINE(WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    THU_NINE_TO_NINE(THURSDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    FRI_NINE_TO_NINE(FRIDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    SAT_NINE_TO_NINE(SATURDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    SUN_CLOSED(SUNDAY, LocalTime.MAX, LocalTime.MAX, false, true),
    SUN_NINE_TO_NINE(SUNDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false),
    HOL_NINE_TO_NINE(HOLIDAY, LocalTime.of(9, 0), LocalTime.of(21, 0), false, false);

    private final Week week;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean isAllDay;
    private final boolean closed;

    OperationInfoFixture(Week week, LocalTime startTime, LocalTime endTime, boolean isAllDay, boolean closed) {
        this.week = week;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAllDay = isAllDay;
        this.closed = closed;
    }

    public OperationInfo 생성() {
        return 생성(null);
    }

    public OperationInfo 생성(Long id) {
        return 기본_정보_생성().build();
    }

    public OperationInfo.OperationInfoBuilder 기본_정보_생성() {
        return OperationInfo.builder()
                .week(this.week)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .isAllDay(this.isAllDay)
                .closed(this.closed);
    }

    public OperationInfo 스터디카페_생성(Studycafe studycafe) {
        OperationInfo operationInfo = 기본_정보_생성().build();
        operationInfo.setStudycafe(studycafe);

        return operationInfo;
    }
}
