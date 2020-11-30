package wrss.wz.website.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromGetEnrollmentResponse {

    private UUID promEnrollmentId;
    private PromGetEnrollmentPersonResponse mainPerson;
    private PromGetEnrollmentPersonResponse partner;
    private String message;
}
