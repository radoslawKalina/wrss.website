package wrss.wz.website.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentEntity {

    @Id
    @NotNull
    @GeneratedValue
    private Long promEnrollmentId;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "prom_person_id")
    private PromPersonEntity mainPerson;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "prom_person_id")
    private PromPersonEntity partner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "user_id")
    private StudentEntity user;

    private String message;
}