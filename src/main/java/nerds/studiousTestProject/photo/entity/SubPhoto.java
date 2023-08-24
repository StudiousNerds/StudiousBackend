package nerds.studiousTestProject.photo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "studycafe_id")
    private Studycafe studycafe;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "path", nullable = false)
    private String path;

    @Builder
    public SubPhoto(Long id, Room room, Studycafe studycafe, Review review, String path) {
        this.id = id;
        this.room = room;
        this.studycafe = studycafe;
        this.review = review;
        this.path = path;
    }

    public void setStudycafe(Studycafe studycafe) {
        if (studycafe != null) {
            this.studycafe = studycafe;
        }
    }

    public void setRoom(Room room) {
        if (room != null) {
            this.room = room;
        }
    }
}
