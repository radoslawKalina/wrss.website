package wrss.wz.website.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class TripEntity {

    //TODO: Add custom validation for index

    @Id
    @NotNull
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Integer index;

    @NotNull
    private String shirtSize;

    @NotNull
    private String transport;

    private String university;
    private String faculty;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public TripEntity(@NotNull Long id, @NotNull String name, @NotNull String surname, @NotNull @Email String email,
                      @NotNull Integer index, @NotNull String shirtSize, @NotNull String transport, String university, String faculty) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.index = index;
        this.shirtSize = shirtSize;
        this.transport = transport;
        this.university = university;
        this.faculty = faculty;
    }
}