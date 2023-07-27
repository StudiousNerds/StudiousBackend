package nerds.studiousTestProject.studycafe.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nerds.studiousTestProject.studycafe.repository.StudycafeDslRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestQueryDslConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    /**
     * 테스트할 Repo 의존관계 주입
     * @return StudycafeDslRepository
     */
    @Bean
    public StudycafeDslRepository studycafeDslRepository() {
        return new StudycafeDslRepository(jpaQueryFactory());
    }
}
