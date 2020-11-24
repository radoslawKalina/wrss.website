package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentPersonRequest {

    @NotBlank(message = "Name field can't be blank")
    private String name;

    @NotBlank(message = "Surname field can't be blank")
    private String surname;
}