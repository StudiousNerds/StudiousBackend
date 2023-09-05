package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ReserveRequest {

    @NotNull
    private ReserveUser user;

    @NotNull
    private ReservationInfo reservation;

    @Nullable
    private List<PaidConvenience> paidConveniences;

}
