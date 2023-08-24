package nerds.studiousTestProject.studycafe.validator.search;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;

@Slf4j
public class SearchDateTimeCheckValidator implements ConstraintValidator<SearchDateTimeCheck, SearchRequest> {

    @Override
    public void initialize(SearchDateTimeCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SearchRequest searchRequest, ConstraintValidatorContext context) {
        return !(searchRequest.getDate() == null && (searchRequest.getStartTime() != null || searchRequest.getEndTime() != null));
    }
}
