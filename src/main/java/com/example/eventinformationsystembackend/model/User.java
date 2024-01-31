package com.example.eventinformationsystembackend.model;

import com.example.eventinformationsystembackend.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "profile_picture_path")
    private String profilePicturePath;

    @Column(name = "credit_card_number")
    private String creditCardNumber;

    @Column(name = "credit_card_cvv")
    private String creditCardCvv;

    @Column(name = "user_role")
    @Enumerated(value = EnumType.STRING)
    private UserRole userRole;

    @Column(name = "is_locked", nullable = false, columnDefinition = "TINYINT(1) DEFAULT FALSE")
    private Boolean isLocked;

    @Column(name = "is_enabled", nullable = false, columnDefinition = "TINYINT(1) DEFAULT FALSE")
    private Boolean isEnabled;

    @Column(name = "profile_picture_name")
    private String profilePictureName;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<SupportTicket> supportTickets;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ConfirmationToken> confirmationTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "replier")
    private List<SupportTicketReply> supportTicketReply;

    @OneToOne
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "refresh_token_id")
    private RefreshToken refreshToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (isLocked) {
            throw new LockedException(ACCOUNT_LOCKED_EXCEPTION);
        }

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (!isEnabled) {
            throw new DisabledException(ACCOUNT_NOT_ENABLED_EXCEPTION);
        }

        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", profilePicturePath='" + profilePicturePath + '\'' +
                ", creditCardNumber='" + creditCardNumber + '\'' +
                ", creditCardCvv='" + creditCardCvv + '\'' +
                '}';
    }
}
