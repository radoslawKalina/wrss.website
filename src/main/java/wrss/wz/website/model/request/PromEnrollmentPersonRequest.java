package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentPersonRequest {

    @NotBlank(message = "Name field can't be blank")
    private String name;

    @NotBlank(message = "Surname field can't be blank")
    private String surname;

    @Email(message = "Wrong value for email field. You need to provide email address")
    @NotBlank(message = "Email field can't be blank")
    private String email;

    @NotNull(message = "Phone number field can't be blank")
    @Size(min = 9, max = 9, message = "Wrong value for phone number field. You need to provide valid phone number")
    private String phoneNumber;

    @NotNull(message = "fromAGH field can't be null")
    private boolean fromAGH;

    private Integer index;
    private String faculty;
    private String field;
    private Integer year;
}