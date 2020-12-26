package wrss.wz.website.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wrss.wz.website.H2TestConfiguration;
import wrss.wz.website.WebsiteApplication;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.service.interfaces.PromAdminService;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {WebsiteApplication.class, H2TestConfiguration.Config.class})
@Sql(value = "/sql/addUsers.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "/sql/addPromEnrollments.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "/sql/truncateAllTables.sql", executionPhase = AFTER_TEST_METHOD)
@SqlMergeMode(MERGE)
public class PromAdminServiceH2Test {

    @Autowired
    private PromAdminService promAdminService;

    @Test
    @Sql(value = "/sql/truncateAllTables.sql", executionPhase = BEFORE_TEST_METHOD)
    public void shouldReturnEmptyListWhenNoEnrollmentsCreated() {

        List<PromEnrollmentResponse> responseList = promAdminService.getAllEnrollments();
        assertThat(responseList).hasSize(0);
    }

    @Test
    public void shouldReturnAllEnrollments() {

        List<PromEnrollmentResponse> responseList = promAdminService.getAllEnrollments();
        assertThat(responseList).hasSize(3);
    }

    @Test
    public void shouldReturnEnrollmentByEnrollmentIdWhenEnrollmentTypeIsPair() {

        UUID enrollmentId = UUID.fromString("0d7f96e5-1e06-4405-bf1a-c4c4a010fd27");
        PromEnrollmentPersonResponse mainPerson = new PromEnrollmentPersonResponse("firstName", "firstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);
        PromEnrollmentPersonResponse partner = new PromEnrollmentPersonResponse("thirdName", "thirdSurname",
                "third.user@gmail.com", "345678901", true, 301245, "WIMIP", "IMiM", 3);

        PromEnrollmentResponse response = promAdminService.getEnrollment(enrollmentId);

        assertThat(response.getPromEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(response.getMainPerson()).isEqualTo(mainPerson);
        assertThat(response.getPartner()).isEqualTo(partner);
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("firstMessage");
    }

    @Test
    public void shouldReturnEnrollmentByEnrollmentIdWhenEnrollmentTypeIsSingle() {

        UUID enrollmentId = UUID.fromString("355a5774-8ec6-4893-aef2-db98f7709ef3");
        PromEnrollmentPersonResponse mainPerson = new PromEnrollmentPersonResponse("secondName", "secondSurname",
                "second.user@gmail.com", "234567890", true, 284211, "WZ", "IiE", 4);

        PromEnrollmentResponse response = promAdminService.getEnrollment(enrollmentId);

        assertThat(response.getPromEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(response.getMainPerson()).isEqualTo(mainPerson);
        assertThat(response.getPartner()).isNull();
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("thirdMessage");
    }
}
