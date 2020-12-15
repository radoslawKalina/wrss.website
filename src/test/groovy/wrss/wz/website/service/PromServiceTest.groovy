package wrss.wz.website.service

import org.modelmapper.ModelMapper
import spock.lang.Specification
import spock.lang.Subject
import wrss.wz.website.entity.PromEnrollmentEntity
import wrss.wz.website.entity.PromPersonEntity
import wrss.wz.website.entity.RoleEntity
import wrss.wz.website.entity.StudentEntity
import wrss.wz.website.entity.TripEntity
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException
import wrss.wz.website.exception.custom.RecordBelongingException
import wrss.wz.website.exception.custom.UserDoesNotExistException
import wrss.wz.website.model.request.PromEnrollmentPersonRequest
import wrss.wz.website.repository.PromEnrollmentRepository
import wrss.wz.website.repository.PromPersonRepository
import wrss.wz.website.repository.UserRepository

class PromServiceTest extends Specification {

    private StudentEntity user = new StudentEntity(UUID.randomUUID(), "First", "test@gmail.com",
            "password", new ArrayList<RoleEntity>(), new ArrayList<TripEntity>(), new ArrayList<PromEnrollmentEntity>())

    private StudentEntity secondUser = new StudentEntity(UUID.randomUUID(), "Second", "second.test@gmail.com",
            "password", new ArrayList<RoleEntity>(), new ArrayList<TripEntity>(), new ArrayList<PromEnrollmentEntity>())

    private PromPersonEntity firstPerson = new PromPersonEntity(UUID.randomUUID(), "First", "Surname",
            "first@gmail.com", "123456789", true, 282828, "WZ", "IiE", 5)

    private PromPersonEntity secondPerson = new PromPersonEntity(UUID.randomUUID(), "Second", "Surname",
            "second@gmail.com", "123456789", false, null, null, null, null)

    private PromEnrollmentEntity enrollment = new PromEnrollmentEntity(UUID.randomUUID(), firstPerson, secondPerson,
            secondUser, false, "message")

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

    def "should throw PromEnrollmentDoesNotExist when enrollment with given id does not exist when get enrollment"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promServiceImpl.get(id, "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw RecordBelongingException when user try to get enrollment which belong to another user"() {
        when:
            promServiceImpl.get(enrollment.getPromEnrollmentId(), "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
        then:
            thrown(RecordBelongingException)
    }

    def "should throw PromEnrollmentDoesNotExist when enrollment with given id does not exist when update enrollment"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promServiceImpl.update(new PromEnrollmentPersonRequest(), id, "mainPerson", "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw RecordBelongingException when user try to update enrollment which belong to another user"() {
        given:
            UUID id = enrollment.getPromEnrollmentId();
        when:
            promServiceImpl.update(new PromEnrollmentPersonRequest(), id, "mainPerson", "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
        then:
            thrown(RecordBelongingException)
    }

    def "should throw PromPersonEntityNotExistException when user try to update but person parameter is incorrect"() {
        given:
            UUID id = enrollment.getPromEnrollmentId();
        when:
            promServiceImpl.update(new PromEnrollmentPersonRequest(), id, "wrongValue", "Second")
        then:
            1 * userRepository.findByUsername("Second") >> secondUser
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
        then:
            thrown(PromPersonEntityNotExistException)
    }

    def "should throw PromPersonEntityNotExistException when user try to update partner data which was null"() {
        given:
            PromEnrollmentEntity singleEnrollment = new PromEnrollmentEntity(UUID.randomUUID(), firstPerson, null,
                user, false, "message")
        when:
            promServiceImpl.update(new PromEnrollmentPersonRequest(), singleEnrollment.getPromEnrollmentId(),
                    "partner", "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(singleEnrollment.getPromEnrollmentId()) >> singleEnrollment
        then:
            thrown(PromPersonEntityNotExistException)
    }

    def "should throw PromEnrollmentDoesNotExist when user try to transfer enrollment which does not exist"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promServiceImpl.transfer(id, "Second", "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw RecordBelongingException when user try to transfer enrollment which belong to another user"() {
        when:
            promServiceImpl.transfer(enrollment.getPromEnrollmentId(), "Second", "First")
        then:
            1 * userRepository.findByUsername("First") >> user
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
        then:
            thrown(RecordBelongingException)
    }

    def "should throw UserDoesNotExistException when user try to transfer enrollment to user which does not exist"() {
        when:
            promServiceImpl.transfer(enrollment.getPromEnrollmentId(), "First", "Second")
        then:
            1 * userRepository.findByUsername("Second") >> secondUser
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
            1 * userRepository.findByUsername("First") >> null
        then:
            thrown(UserDoesNotExistException)
    }
}
