package wrss.wz.website.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class RoleEntity {

    @Id
    @NotNull
    @GeneratedValue
    private Long roleId;

    @NotBlank
    private String role;

    @ManyToMany(mappedBy = "roles")
    private List<StudentEntity> students;

}