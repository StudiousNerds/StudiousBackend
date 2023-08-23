package nerds.studiousTestProject.studycafe.validator.register;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterRoomCheckValidator.class)
public @interface RegisterRoomCheck {
    String message() default "최대 인원수는 최소 인원 수보다 커야합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
