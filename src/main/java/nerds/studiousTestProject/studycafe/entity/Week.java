package nerds.studiousTestProject.studycafe.entity;

import java.time.LocalDate;

public enum Week {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    HOLIDAY;

    public static Week of(LocalDate date) {
        return Week.valueOf(date.getDayOfWeek().name());
    }
}
