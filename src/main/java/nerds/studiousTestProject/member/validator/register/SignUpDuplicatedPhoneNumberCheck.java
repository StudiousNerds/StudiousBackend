package nerds.studiousTestProject.member.validator.register;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SignUpDuplicatedPhoneNumberCheckValidator.class)
public @interface SignUpDuplicatedPhoneNumberCheck {
    String message() default "해당 전화번호로 가입한 계정이 이미 존재합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
