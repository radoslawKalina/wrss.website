package wrss.wz.website.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name="user")
public class UserEntity {

    @Id
    @NotNull
    @GeneratedValue
    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private String name;

    @NotNull
    private String username;

    @NotNull
    private String encryptedPassword;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TripEntity> trips;

    public UserEntity(@NotNull Long id, @NotNull String name, @NotNull String username, @NotNull String encryptedPassword) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }
}