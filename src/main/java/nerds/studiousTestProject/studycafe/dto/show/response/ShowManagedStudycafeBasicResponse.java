package nerds.studiousTestProject.studycafe.dto.show.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Builder
@Data
public class ShowManagedStudycafeBasicResponse {
    private Long id;
    private String name;
    private String address;
    private String photo;

    public static ShowManagedStudycafeBasicResponse from(Studycafe studycafe) {
        return ShowManagedStudycafeBasicResponse.builder()
                .id(studycafe.getId())
                .name(studycafe.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .photo(studycafe.getPhoto())
                .build();
    }
}
