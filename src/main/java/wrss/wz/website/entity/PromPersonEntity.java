package wrss.wz.website.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromPersonEntity {

    @Id
    @NotNull
    @GeneratedValue
    @Column(name = "prom_person_id")
    private Long promPersonId;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;
}