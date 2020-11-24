package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentRequest {

    @NotNull(message = "Missing type field. You need to specify is it a single person or pair enrollment")
    @Pattern(regexp = "single|pair", message = "Wrong value for type field. Use single or pair")
    private String type;

    @NotNull(message = "Missing mainPerson field")
    @Valid
    private PromEnrollmentPersonRequest mainPerson;

    @Valid
    private PromEnrollmentPersonRequest partner;

    private String message;
}