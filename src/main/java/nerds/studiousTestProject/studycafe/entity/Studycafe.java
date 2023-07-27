package nerds.studiousTestProject.studycafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
