package wrss.wz.website.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromGetEnrollmentResponse {

    private String promEnrollmentCustomId;
    private PromGetEnrollmentPersonResponse mainPerson;
    private PromGetEnrollmentPersonResponse partner;
    private String message;
}
