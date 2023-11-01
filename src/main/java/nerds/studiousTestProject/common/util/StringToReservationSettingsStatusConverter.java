package nerds.studiousTestProject.common.util;

import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import org.springframework.core.convert.converter.Converter;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.CANT_CONVERT_TO_RESERVATION_SETTING_STATUS;

public class StringToReservationSettingsStatusConverter implements Converter<String, ReservationSettingsStatus> {
    @Override
    public ReservationSettingsStatus convert(String status) {
        if(status == null) return null;
        try {
            return ReservationSettingsStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(CANT_CONVERT_TO_RESERVATION_SETTING_STATUS);
        }
    }
}
