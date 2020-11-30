package wrss.wz.website.service

import org.modelmapper.ModelMapper
import spock.lang.Specification
import spock.lang.Subject
import wrss.wz.website.entity.PromEnrollmentEntity
import wrss.wz.website.entity.PromPersonEntity
import wrss.wz.website.entity.RoleEntity
import wrss.wz.website.entity.StudentEntity
import wrss.wz.website.entity.TripEntity
import wrss.wz.website.model.response.PromEnrollmentResponse
import wrss.wz.website.repository.PromEnrollmentRepository
import wrss.wz.website.repository.PromPersonRepository
import wrss.wz.website.repository.UserRepository

class PromServiceTest extends Specification {

    private StudentEntity user = new StudentEntity(UUID.randomUUID(), "Test", "test@gmail.com",
            "password", new ArrayList<RoleEntity>(), new ArrayList<TripEntity>(), new ArrayList<PromEnrollmentEntity>())

    private PromPersonEntity firstPerson = new PromPersonEntity(UUID.randomUUID(), "First", "Test",
            "first.test@gmail.com", "123456789", true, 282828, "WZ", "IiE", 5)

    private PromPersonEntity secondPerson = new PromPersonEntity(UUID.randomUUID(), "Second", "Test",
            "second.test@gmail.com", "123456789", true, 292929, "WIMIR", "IMiM", 2)

    private PromPersonEntity thirdPerson = new PromPersonEntity(UUID.randomUUID(), "Third", "Test",
            "third.test@gmail.com", "123456789", false, 401201, null, null, null)

    private PromEnrollmentEntity firstEnrollment = new PromEnrollmentEntity(UUID.randomUUID(), firstPerson, null,
            user, false, "message")

    private PromEnrollmentEntity secondEnrollment = new PromEnrollmentEntity(UUID.randomUUID(), secondPerson, thirdPerson,
            user, false, "message")

    @Subject
    private PromServiceImpl promServiceImpl

    private UserRepository userRepository
    private PromPersonRepository promPersonRepository
    private PromEnrollmentRepository promEnrollmentRepository
    private ModelMapper modelMapper

    def setup() {
        userRepository = Mock()
        promPersonRepository = Mock()
        promEnrollmentRepository = Mock()
        modelMapper = new ModelMapper()

        promServiceImpl = new PromServiceImpl(modelMapper, userRepository, promPersonRepository, promEnrollmentRepository)
    }

    def "should return all enrollments added by user"() {
        when:
            List<PromEnrollmentResponse> responseList = promServiceImpl.getAll("Test")
        then:
            1 * userRepository.findByUsername("Test") >> user
            1 * promEnrollmentRepository.findAllByUser(user) >> [firstEnrollment, secondEnrollment]
        then:
            responseList.size() == 2
    }

    def "should return empty list when user do not have enrollments"() {
        when:
            List<PromEnrollmentResponse> responseList = promServiceImpl.getAll("Test")
        then:
            1 * userRepository.findByUsername("Test") >> user
            1 * promEnrollmentRepository.findAllByUser(user) >> []
        then:
        responseList.size() == 0
    }
}
