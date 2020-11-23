package wrss.wz.website.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromSignUpRequest {

    @NotNull
    private boolean together;

    @NotNull
    private PromSignUpPersonRequest mainPerson;

    private PromSignUpPersonRequest partner;
    private String message;
}