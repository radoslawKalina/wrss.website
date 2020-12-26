package wrss.wz.website.service

import org.modelmapper.ModelMapper
import spock.lang.Specification
import spock.lang.Subject
import wrss.wz.website.entity.*
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException
import wrss.wz.website.model.request.PaidRequest
import wrss.wz.website.model.request.PromEnrollmentPersonRequest
import wrss.wz.website.repository.PromEnrollmentRepository
import wrss.wz.website.repository.PromPersonRepository

class PromAdminServiceTest extends Specification {

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
    private PromAdminServiceImpl promAdminServiceImpl

    private PromPersonRepository promPersonRepository
    private PromEnrollmentRepository promEnrollmentRepository

    private PromBase promUtils;
    private ModelMapper modelMapper

    def setup() {
        promPersonRepository = Mock()
        promEnrollmentRepository = Mock()

        promUtils = new PromBase(modelMapper, promPersonRepository, promEnrollmentRepository)
        modelMapper = new ModelMapper()

        promAdminServiceImpl = new PromAdminServiceImpl(modelMapper, promUtils)
    }

    def "should throw PromEnrollmentDoesNotExist when enrollment with given id does not exist when get enrollment"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promAdminServiceImpl.getEnrollment(id)
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw PromEnrollmentDoesNotExist when enrollment with given id does not exist when update enrollment"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promAdminServiceImpl.updateEnrollment(new PromEnrollmentPersonRequest(), id, "mainPerson")
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw PromPersonEntityNotExistException when user try to update but person parameter is incorrect"() {
        given:
            UUID id = enrollment.getPromEnrollmentId();
        when:
            promAdminServiceImpl.updateEnrollment(new PromEnrollmentPersonRequest(), id, "wrongValue")
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(enrollment.getPromEnrollmentId()) >> enrollment
        then:
            thrown(PromPersonEntityNotExistException)
    }

    def "should throw PromPersonEntityNotExistException when user try to update partner data which was null"() {
        given:
            PromEnrollmentEntity singleEnrollment = new PromEnrollmentEntity(UUID.randomUUID(), firstPerson, null,
                    user, false, "message")
        when:
            promAdminServiceImpl.updateEnrollment(new PromEnrollmentPersonRequest(), singleEnrollment.getPromEnrollmentId(),
                    "partner")
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(singleEnrollment.getPromEnrollmentId()) >> singleEnrollment
        then:
            thrown(PromPersonEntityNotExistException)
    }

    def "should throw PromEnrollmentDoesNotExist when user try to delete enrollment which does not exist"() {
        given:
            UUID id = UUID.randomUUID()
        when:
            promAdminServiceImpl.deleteEnrollment(id)
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }

    def "should throw PromEnrollmentDoesNotExist when user try to change paid status of enrollment which does not exist"() {
        given:
            PaidRequest paidRequest = new PaidRequest(true)
            UUID id = UUID.randomUUID()
        when:
            promAdminServiceImpl.changeEnrollmentPaidStatus(id, paidRequest)
        then:
            1 * promEnrollmentRepository.findByPromEnrollmentId(id) >> null
        then:
            thrown(PromEnrollmentDoesNotExist)
    }
}
