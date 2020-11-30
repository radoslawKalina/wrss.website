package wrss.wz.website.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentResponse {

    private UUID promEnrollmentId;
    private PromEnrollmentPersonResponse mainPerson;
    private PromEnrollmentPersonResponse partner;
    private boolean paid;
    private String message;
}
