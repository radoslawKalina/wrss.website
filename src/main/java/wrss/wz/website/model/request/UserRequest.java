package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name field can't be blank")
    private String name;

    @Email(message = "Wrong value for username field. You need to provide email address")
    @NotBlank(message = "Username field can't be blank")
    private String username;

    @NotBlank(message = "Password field can't be blank")
    private String password;
}