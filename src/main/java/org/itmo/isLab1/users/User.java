package org.itmo.isLab1.users;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.itmo.isLab1.common.entity.BaseEntity;
import org.itmo.isLab1.common.utils.datetime.ZonedDateTimeConverter;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art2art_users")
public class User implements BaseEntity, UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "art2art_users_id_seq")
    @SequenceGenerator(name = "art2art_users_id_seq", sequenceName = "art2art_users_id_seq", allocationSize = 1)
    private Long id;
    
    @NotBlank
    @Column(name="email", nullable=false, unique=true)
    private String username;

    @NotBlank
    @Column(name="name", nullable=false)
    private String name;

    @NotBlank
    @Column(name="surname", nullable=false)
    private String surname;

    @JsonIgnore
    @ToString.Exclude
    @Column(name="password_hash", nullable=false)
    private String password;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write="?::art2art_user_role_enum")
    @Column(name="role", nullable=false)
    private Role role;

    @Column(name="is_active", nullable=false)
    private Boolean is_active;

    @CreationTimestamp
    @Column(name="created_at", nullable=false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updatedAt;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnore
    public boolean isAdmin() {
        return this.role.equals(Role.ROLE_SUPERADMIN);
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return is_active;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return is_active;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return is_active;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return is_active;
    }
}