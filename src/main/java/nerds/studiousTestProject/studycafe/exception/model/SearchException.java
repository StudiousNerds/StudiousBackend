package nerds.studiousTestProject.studycafe.exception.model;

import nerds.studiousTestProject.studycafe.exception.message.SearchExceptionMessage;

public class SearchException extends RuntimeException {
    public SearchException(String message) {
        super(message);
    }

    public SearchException(SearchExceptionMessage searchExceptionMessage) {
        super(searchExceptionMessage.getMessage());
    }
}
