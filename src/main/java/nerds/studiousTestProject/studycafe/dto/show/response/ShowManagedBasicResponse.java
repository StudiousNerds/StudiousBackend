package nerds.studiousTestProject.studycafe.dto.show.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Builder
@Data
public class ShowManagedBasicResponse {
    private Long id;
    private String name;
    private String address;
    private String photo;

    public static ShowManagedBasicResponse from(Studycafe studycafe) {
        return ShowManagedBasicResponse.builder()
                .id(studycafe.getId())
                .name(studycafe.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .photo(studycafe.getPhoto())
                .build();
    }
}
