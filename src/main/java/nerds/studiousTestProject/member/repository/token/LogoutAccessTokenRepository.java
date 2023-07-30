package nerds.studiousTestProject.member.repository.token;

import nerds.studiousTestProject.member.entity.token.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRepository extends CrudRepository<LogoutAccessToken, String> {
}
