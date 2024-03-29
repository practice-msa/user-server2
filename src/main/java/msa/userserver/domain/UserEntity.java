package msa.userserver.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String email;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false, unique = true)
    private String userId;

    @Embedded
    private Password password;

    @Builder
    public UserEntity(String email, String name, String userId, String password) {
        this.email = email;
        this.name = name;
        this.userId = userId;
        this.password = new Password(password);
    }

}
