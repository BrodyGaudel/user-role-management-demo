package org.mounanga.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @Column(nullable = false)
    private boolean passwordNeedsToBeChanged = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private Profile profile;

    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="user_role",joinColumns = @JoinColumn(name="user_id") , inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles;

    public boolean isEnabled(){
        return Boolean.TRUE.equals(this.enabled);
    }

    public boolean isDisabled(){
        return Boolean.FALSE.equals(this.enabled);
    }

    public void addRole(Role role){
        if(this.roles == null){
            this.roles = new ArrayList<>();
        }
        if(role != null && !roles.contains(role)){
            this.roles.add(role);
        }
    }

    public void removeRole(Role role){
        if(this.roles != null && role != null && role.getName().equals("USER")){
                this.roles.remove(role);
        }
    }

    public boolean isSuperAdmin(){
        return roles.stream()
                .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));
    }

    public void markPasswordAsChanged() {
        this.passwordNeedsToBeChanged = false;
    }


}
