package nerds.studiousTestProject.studycafe.validator.search;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;

@Slf4j
public class SearchTimeCheckValidator implements ConstraintValidator<SearchTimeCheck, SearchRequest> {
    @Override
    public void initialize(SearchTimeCheck constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(SearchRequest searchRequest, ConstraintValidatorContext context) {
        return !((searchRequest.getStartTime() != null && searchRequest.getEndTime() != null) && searchRequest.getStartTime().isAfter(searchRequest.getEndTime()));
    }
}
