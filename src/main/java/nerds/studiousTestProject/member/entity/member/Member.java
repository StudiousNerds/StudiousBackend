package nerds.studiousTestProject.member.entity.member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = true)
    private Long providerId;

    @Column(updatable = false, nullable = false)
    private String email;   // 타입이 다르면 중복 가능

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberType type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_date", unique = true)
    @CreatedDate
    private Date createdDate;

    @Column(name = "photo", nullable = true)
    private String photo; // 사용자 프로필 사진

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Column(nullable = false)
    private Boolean usable;

    @Column(nullable = true)
    private LocalDate resignedDate;

    @Builder
    public Member(Long id, Long providerId, String email, String password, MemberType type, String name, String nickname, Date birthday, String phoneNumber, Date createdDate, String photo, Boolean usable, LocalDate resignedDate) {
        this.id = id;
        this.providerId = providerId;
        this.email = email;
        this.password = password;
        this.type = type;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.createdDate = createdDate;
        this.photo = photo;
        this.usable = usable;
        this.resignedDate = resignedDate;
    }

    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
            role.setMember(this);
        }
    }

    public void addBookmark(Bookmark bookmark) {
        if (bookmark != null) {
            bookmarks.add(bookmark);
            bookmark.setMember(this);
        }
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void registerBookmark(String studycafeName){
//        bookmarks.add(studycafeName);
    }

    public void deleteBookmark(String studycafeName){
        bookmarks.remove(studycafeName);
    }

    public void withdraw() {
        this.usable = false;
        this.resignedDate = LocalDate.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(r -> r.getValue().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usable;
    }
}
