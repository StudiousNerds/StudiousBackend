package nerds.studiousTestProject.studycafe.validator.manage;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nerds.studiousTestProject.studycafe.validator.search.SearchDateTimeCheckValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SearchDateTimeCheckValidator.class)
public @interface RegisterAnnouncementCheck {
    String message() default "시작 기간은 끝 기간보다 클 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
