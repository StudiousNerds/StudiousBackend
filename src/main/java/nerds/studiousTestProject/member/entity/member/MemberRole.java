package nerds.studiousTestProject.member.entity.member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberRole {
    USER(ROLES.USER),
    ADMIN(ROLES.ADMIN),
    SUPER_ADMIN(ROLES.SUPER_ADMIN);

    private final String value;

    public final String getValue() {
        return value;
    }

    /**
     * 어노테이션에서 요소가 항상 상수여야 하므로 아래 값을 사용한다.
     */
    public static class ROLES {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    }
}
