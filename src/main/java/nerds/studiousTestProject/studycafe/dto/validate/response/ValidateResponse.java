package nerds.studiousTestProject.studycafe.dto.validate.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ValidateResponse {
    private boolean available;

    @Builder
    public ValidateResponse(boolean available) {
        this.available = available;
    }
}
