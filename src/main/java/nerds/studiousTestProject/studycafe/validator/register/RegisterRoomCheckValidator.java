package nerds.studiousTestProject.studycafe.validator.register;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nerds.studiousTestProject.studycafe.dto.register.request.RoomInfoRequest;

public class RegisterRoomCheckValidator implements ConstraintValidator<RegisterRoomCheck, RoomInfoRequest> {
    @Override
    public void initialize(RegisterRoomCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RoomInfoRequest roomInfoRequest, ConstraintValidatorContext context) {
        return roomInfoRequest.getMinHeadCount() <= roomInfoRequest.getMaxHeadCount();
    }
}
