package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Studycafe {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String tel;
    private String photo;
    private LocalTime startTime;
    private LocalTime endTime;

    @Builder
    public Studycafe(Long id, String name, String tel, String photo, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.photo = photo;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
