package nerds.studiousTestProject.studycafe.dto.valid.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ValidResponse {
    private boolean available;

    @Builder
    public ValidResponse(boolean available) {
        this.available = available;
    }
}
