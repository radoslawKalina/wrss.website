package wrss.wz.website.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromEnrollmentPersonResponse {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private boolean fromAGH;
    private Integer index;
    private String faculty;
    private String field;
    private Integer year;
}
