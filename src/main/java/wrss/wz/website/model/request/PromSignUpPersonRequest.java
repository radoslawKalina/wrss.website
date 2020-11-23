package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromSignUpPersonRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String surname;
}