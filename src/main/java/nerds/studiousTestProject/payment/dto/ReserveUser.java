package nerds.studiousTestProject.payment.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveUser {

    @NotBlank
    private String name;

    @Size(min = 11, max = 11)
    private String phoneNumber;

    @Nullable
    private String request;

}
