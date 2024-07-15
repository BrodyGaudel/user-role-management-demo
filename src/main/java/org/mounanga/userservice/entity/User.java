package org.mounanga.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.mounanga.userservice.enums.Gender;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String placeOfBirth;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false, unique = true)
    private String nip;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled;

    private LocalDateTime lastLogin;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(updatable = false)
    @LastModifiedDate
    private String createdBy;

    @Column(insertable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @Column(nullable = false)
    private boolean passwordNeedsToBeChanged = false;

    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="user_role",joinColumns = @JoinColumn(name="user_id") , inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles;

    public String getFullName(){
        return firstname + " " + lastname;
    }

    public void addRole(Role role){
        if(roles == null){
            roles = new ArrayList<>();
        }
        if(role != null && !roles.contains(role)){
            roles.add(role);
        }
    }

    public void removeRole(Role role){
        if(roles != null && role != null && !role.getName().equals("USER")){
            roles.remove(role);
        }
    }

    public boolean isEnabled(){
        if(enabled == null){
            return false;
        } else {
            return enabled == Boolean.TRUE;
        }
    }

    public void markPasswordAsChanged() {
        this.passwordNeedsToBeChanged = false;
    }
}
