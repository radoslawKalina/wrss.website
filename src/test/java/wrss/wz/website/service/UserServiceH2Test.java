package wrss.wz.website.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wrss.wz.website.H2TestConfiguration;
import wrss.wz.website.WebsiteApplication;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.model.request.UserRequest;
import wrss.wz.website.model.response.UserResponse;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {WebsiteApplication.class, H2TestConfiguration.Config.class})
@Sql(value = "/sql/addUserRoles.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "/sql/truncateAllTables.sql", executionPhase = AFTER_TEST_METHOD)
@SqlMergeMode(MERGE)
public class UserServiceH2Test {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Test
    public void shouldCreateUser() {

        String username = "username@gmail.com";
        UserRequest userRequest = new UserRequest("name", username, "password");

        UserResponse response = userService.createUser(userRequest);

        assertThat(response.getUserId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo(username);

        StudentEntity user = userRepository.findByUsername(username);
        assertThat(user.getUserId()).isNotNull();
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getName()).isEqualTo("name");
        assertThat(user.getPassword()).isNotEqualTo("password");
        assertThat(encoder.matches("password", user.getPassword())).isTrue();
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles().get(0).getRole()).isEqualTo("STUDENT");
        assertThat(user.getRoles().get(0).getRole()).isNotEqualTo("ADMIN");
        assertThat(user.getRoles().get(0).getRole()).isNotEqualTo("SUPER_ADMIN");
    }
}
