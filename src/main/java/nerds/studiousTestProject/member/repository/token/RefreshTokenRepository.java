package nerds.studiousTestProject.member.repository.token;

import nerds.studiousTestProject.member.entity.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
}
