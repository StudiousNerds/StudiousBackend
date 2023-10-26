package nerds.studiousTestProject.member.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.bookmark.entity.Bookmark;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = true)
    private Long providerId;

    @Column(updatable = false, nullable = false)
    private String email;   // 타입이 다르면 중복 가능

    @Column(name = "password", nullable = true)
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MemberType type;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "birthday", nullable = true)
    private LocalDate birthday;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_date", unique = true)
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "photo", nullable = true)
    private String photo; // 사용자 프로필 사진

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Column(name = "usable", nullable = false)
    private Boolean usable;

    @Column(name = "resigned_date", nullable = true)
    private LocalDate resignedDate;

    @Builder
    public Member(Long id, Long providerId, String email, String password, MemberType type, String name, String nickname, LocalDate birthday, String phoneNumber, LocalDate createdDate, String photo, Boolean usable, LocalDate resignedDate) {
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

    public void updatePhoto(String photo) {
        if (photo != null && !photo.isEmpty()) {
            this.photo = photo;
        }
    }

    public void updateNickname(String nickname) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
    }

    public void updatePassword(String encodedPassword) {
        if (encodedPassword != null && !encodedPassword.isEmpty()) {
            this.password = encodedPassword;
        }
    }

    public void deleteBookmark(Bookmark bookmark){
        bookmarks.remove(bookmark);
    }

    public void withdraw() {
        this.usable = false;
        this.resignedDate = LocalDate.now();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(r -> r.getValue().name())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return usable;
    }
}
