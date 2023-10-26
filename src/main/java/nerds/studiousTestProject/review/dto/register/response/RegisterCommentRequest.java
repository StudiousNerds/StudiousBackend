package nerds.studiousTestProject.review.dto.register.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class RegisterCommentRequest {
    @Length(min = 10, message = "댓글은 최소 10자 이상이여야 합니다.")
    @NotBlank
    private String comment;
}
