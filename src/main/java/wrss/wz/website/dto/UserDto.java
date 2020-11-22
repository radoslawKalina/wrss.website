package wrss.wz.website.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String userId;
    private String name;
    private String surname;
    private String username;
    private String password;
    private String email;
    private Integer index;
    private String shirtSize;

}