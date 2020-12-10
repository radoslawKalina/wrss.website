package wrss.wz.website.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wrss.wz.website.H2TestConfiguration;
import wrss.wz.website.WebsiteApplication;
import wrss.wz.website.entity.PromEnrollmentEntity;
import wrss.wz.website.entity.PromPersonEntity;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.repository.PromEnrollmentRepository;
import wrss.wz.website.repository.PromPersonRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.service.interfaces.PromService;


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
public class PromServiceH2Test {

    @Autowired
    private PromService promService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PromEnrollmentRepository promEnrollmentRepository;

    @Autowired
    private PromPersonRepository promPersonRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void shouldReturnEmptyListWhenGetAllEnrollmentsButAnyBelongToUser() {

        List<PromEnrollmentResponse> responseList = promService.getAll("third.user@gmail.com");

        assertThat(responseList).hasSize(0);
    }

    @Test
    public void shouldReturnAllEnrollmentsBelongingToUser() {

        List<PromEnrollmentResponse> responseList = promService.getAll("first.user@gmail.com");

        assertThat(responseList).hasSize(2);
    }

    @Test
    public void shouldReturnEnrollmentByEnrollmentIdWhenEnrollmentTypeIsPair() {

        UUID enrollmentId = UUID.fromString("0d7f96e5-1e06-4405-bf1a-c4c4a010fd27");
        PromEnrollmentPersonResponse mainPerson = new PromEnrollmentPersonResponse("firstName", "firstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);
        PromEnrollmentPersonResponse partner = new PromEnrollmentPersonResponse("thirdName", "thirdSurname",
                "third.user@gmail.com", "345678901", true, 301245, "WIMIP", "IMiM", 3);

        PromEnrollmentResponse response = promService.get(enrollmentId, "first.user@gmail.com");

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

        PromEnrollmentResponse response = promService.get(enrollmentId, "second.user@gmail.com");

        assertThat(response.getPromEnrollmentId()).isEqualTo(enrollmentId);
        assertThat(response.getMainPerson()).isEqualTo(mainPerson);
        assertThat(response.getPartner()).isNull();
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo("thirdMessage");
    }

    @Test
    public void shouldCreatePromEnrollmentWhenEnrollmentTypeIsPair() {

        StudentEntity user = userRepository.findByUsername("first.user@gmail.com");
        PromEnrollmentPersonRequest mainPerson = new PromEnrollmentPersonRequest("firstName", "firstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);
        PromEnrollmentPersonRequest partner = new PromEnrollmentPersonRequest("thirdName", "thirdSurname",
                "third.user@gmail.com", "345678901", true, 301245, "WIMIP", "IMiM", 3);
        PromEnrollmentRequest promEnrollmentRequest = new PromEnrollmentRequest("pair", mainPerson, partner, "firstMessage");

        PromEnrollmentResponse response = promService.signUp(promEnrollmentRequest, "first.user@gmail.com");

        assertThat(response.getPromEnrollmentId()).isNotNull();
        assertThat(response.getMainPerson()).isEqualTo(modelMapper.map(mainPerson, PromEnrollmentPersonResponse.class));
        assertThat(response.getPartner()).isEqualTo(modelMapper.map(partner, PromEnrollmentPersonResponse.class));
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo(promEnrollmentRequest.getMessage());

        PromEnrollmentEntity promEnrollmentEntity = promEnrollmentRepository.findByPromEnrollmentId(response.getPromEnrollmentId());
        assertThat(promEnrollmentEntity).isNotNull();
        assertThat(promEnrollmentEntity.getPromEnrollmentId()).isNotNull();
        assertThat(promEnrollmentEntity.isPaid()).isFalse();
        assertThat(promEnrollmentEntity.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(promEnrollmentEntity.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void shouldCreatePromEnrollmentWhenEnrollmentTypeIsSingle() {

        StudentEntity user = userRepository.findByUsername("first.user@gmail.com");
        PromEnrollmentPersonRequest mainPerson = new PromEnrollmentPersonRequest("firstName", "firstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);
        PromEnrollmentRequest promEnrollmentRequest = new PromEnrollmentRequest("single", mainPerson, null, "firstMessage");

        PromEnrollmentResponse response = promService.signUp(promEnrollmentRequest, "first.user@gmail.com");

        assertThat(response.getPromEnrollmentId()).isNotNull();
        assertThat(response.getMainPerson()).isEqualTo(modelMapper.map(mainPerson, PromEnrollmentPersonResponse.class));
        assertThat(response.getPartner()).isNull();
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo(promEnrollmentRequest.getMessage());

        PromEnrollmentEntity promEnrollmentEntity = promEnrollmentRepository.findByPromEnrollmentId(response.getPromEnrollmentId());
        assertThat(promEnrollmentEntity).isNotNull();
        assertThat(promEnrollmentEntity.getPromEnrollmentId()).isNotNull();
        assertThat(promEnrollmentEntity.getPartner()).isNull();
        assertThat(promEnrollmentEntity.isPaid()).isFalse();
        assertThat(promEnrollmentEntity.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(promEnrollmentEntity.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    public void shouldCreatePromEnrollmentSingleTypeEvenIfPartnerIsNotNullButTypeIsSingle() {

        StudentEntity user = userRepository.findByUsername("first.user@gmail.com");
        PromEnrollmentPersonRequest mainPerson = new PromEnrollmentPersonRequest("firstName", "firstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);
        PromEnrollmentPersonRequest partner = new PromEnrollmentPersonRequest("thirdName", "thirdSurname",
                "third.user@gmail.com", "345678901", true, 301245, "WIMIP", "IMiM", 3);
        PromEnrollmentRequest promEnrollmentRequest = new PromEnrollmentRequest("single", mainPerson, partner, "firstMessage");

        PromEnrollmentResponse response = promService.signUp(promEnrollmentRequest, "first.user@gmail.com");

        assertThat(response.getPromEnrollmentId()).isNotNull();
        assertThat(response.getMainPerson()).isEqualTo(modelMapper.map(mainPerson, PromEnrollmentPersonResponse.class));
        assertThat(response.getPartner()).isNull();
        assertThat(response.isPaid()).isFalse();
        assertThat(response.getMessage()).isEqualTo(promEnrollmentRequest.getMessage());

        PromEnrollmentEntity promEnrollmentEntity = promEnrollmentRepository.findByPromEnrollmentId(response.getPromEnrollmentId());
        assertThat(promEnrollmentEntity).isNotNull();
        assertThat(promEnrollmentEntity.getPromEnrollmentId()).isNotNull();
        assertThat(promEnrollmentEntity.getPartner()).isNull();
        assertThat(promEnrollmentEntity.isPaid()).isFalse();
        assertThat(promEnrollmentEntity.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(promEnrollmentEntity.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void shouldUpdatePromEnrollmentMainPersonData() {

        UUID enrollmentId = UUID.fromString("0d7f96e5-1e06-4405-bf1a-c4c4a010fd27");
        UUID personId = UUID.fromString("027fc173-de01-4ca2-92d5-1431e9967e60");
        PromEnrollmentPersonRequest updatedPerson = new PromEnrollmentPersonRequest("UpdatedFirstName", "UpdatedFirstSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);

        PromEnrollmentPersonResponse response = promService.update(updatedPerson, enrollmentId,
                "main", "first.user@gmail.com");

        assertThat(response).isEqualTo(modelMapper.map(updatedPerson, PromEnrollmentPersonResponse.class));

        PromPersonEntity promPersonEntity = promPersonRepository.findByPromPersonId(personId);
        assertThat(promPersonEntity.getPromPersonId()).isEqualTo(personId);
        assertThat(promPersonEntity.getName()).isEqualTo(updatedPerson.getName());
        assertThat(promPersonEntity.getSurname()).isEqualTo(updatedPerson.getSurname());
        assertThat(promPersonEntity.getEmail()).isEqualTo(updatedPerson.getEmail());
        assertThat(promPersonEntity.getPhoneNumber()).isEqualTo(updatedPerson.getPhoneNumber());
        assertThat(promPersonEntity.getIndex()).isEqualTo(updatedPerson.getIndex());
        assertThat(promPersonEntity.getFaculty()).isEqualTo(updatedPerson.getFaculty());
        assertThat(promPersonEntity.getField()).isEqualTo(updatedPerson.getField());
        assertThat(promPersonEntity.getYear()).isEqualTo(updatedPerson.getYear());
        assertThat(promPersonEntity.isFromAGH()).isEqualTo(updatedPerson.isFromAGH());
    }

    @Test
    public void shouldUpdatePromEnrollmentPartnerData() {

        UUID enrollmentId = UUID.fromString("0d7f96e5-1e06-4405-bf1a-c4c4a010fd27");
        UUID personId = UUID.fromString("86095baa-c75f-4c59-805f-bb0f38bb3904");
        PromEnrollmentPersonRequest updatedPerson = new PromEnrollmentPersonRequest("UpdatedThirdName", "UpdatedThirdSurname",
                "first.user@gmail.com", "123456789", true, 284266, "WZ", "IiE", 2);

        PromEnrollmentPersonResponse response = promService.update(updatedPerson, enrollmentId,
                "partner", "first.user@gmail.com");

        assertThat(response).isEqualTo(modelMapper.map(updatedPerson, PromEnrollmentPersonResponse.class));

        PromPersonEntity promPersonEntity = promPersonRepository.findByPromPersonId(personId);
        assertThat(promPersonEntity.getPromPersonId()).isEqualTo(personId);
        assertThat(promPersonEntity.getName()).isEqualTo(updatedPerson.getName());
        assertThat(promPersonEntity.getSurname()).isEqualTo(updatedPerson.getSurname());
        assertThat(promPersonEntity.getEmail()).isEqualTo(updatedPerson.getEmail());
        assertThat(promPersonEntity.getPhoneNumber()).isEqualTo(updatedPerson.getPhoneNumber());
        assertThat(promPersonEntity.getIndex()).isEqualTo(updatedPerson.getIndex());
        assertThat(promPersonEntity.getFaculty()).isEqualTo(updatedPerson.getFaculty());
        assertThat(promPersonEntity.getField()).isEqualTo(updatedPerson.getField());
        assertThat(promPersonEntity.getYear()).isEqualTo(updatedPerson.getYear());
        assertThat(promPersonEntity.isFromAGH()).isEqualTo(updatedPerson.isFromAGH());
    }
}
