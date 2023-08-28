package nerds.studiousTestProject.studycafe.validator.manage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nerds.studiousTestProject.studycafe.dto.manage.request.AnnouncementRequest;

public class RegisterAnnouncementCheckValidator implements ConstraintValidator<RegisterAnnouncementCheck, AnnouncementRequest> {
    @Override
    public void initialize(RegisterAnnouncementCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AnnouncementRequest announcementRequest, ConstraintValidatorContext context) {
        return announcementRequest.getStartDate().isBefore(announcementRequest.getEndDate());
    }
}
