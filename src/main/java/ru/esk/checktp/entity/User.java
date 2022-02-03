package ru.esk.checktp.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(schema = "check_tp", catalog = "postgres")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "fullname", nullable = false, length = 200)
    private String fullName;
    @ManyToOne(fetch = FetchType.EAGER)
    private Position position;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    public User(String fullName,
                Position position,
                String username,
                String password,
                boolean enabled,
                Role role) {
        this.fullName = fullName;
        this.position = position;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }
}
