package wrss.wz.website.controller

import groovy.json.JsonOutput
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import wrss.wz.website.exception.RestExceptionHandler
import wrss.wz.website.exception.custom.PromEnrollmentDoesNotExist
import wrss.wz.website.exception.custom.PromPersonEntityNotExistException
import wrss.wz.website.model.request.PaidRequest
import wrss.wz.website.model.request.PromEnrollmentPersonRequest
import wrss.wz.website.model.request.PromEnrollmentRequest
import wrss.wz.website.model.response.PromEnrollmentPersonResponse
import wrss.wz.website.model.response.PromEnrollmentResponse
import wrss.wz.website.service.PromAdminServiceImpl

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PromAdminControllerTest extends Specification {

    String BASE_URL = "/api/admin/prom"
    String URL_WITH_ID = "/api/admin/prom/{enrollmentId}"

    UUID enrollmentId = UUID.randomUUID()
    UUID secondEnrollmentId = UUID.randomUUID()

    PromEnrollmentPersonResponse mainPersonResponse = new PromEnrollmentPersonResponse("firstName", "firstSurname",
            "first@gmail.com", "123456789", true, 284242, "WZ", "IiE", 2)

    PromEnrollmentPersonResponse partnerResponse = new PromEnrollmentPersonResponse("secondName", "secondSurname",
            "second@gmail.com", "234567890", false, null, null, null, null)

    PromEnrollmentResponse pairResponse = new PromEnrollmentResponse(enrollmentId, mainPersonResponse, partnerResponse,
            false, "message")
    PromEnrollmentResponse singleResponse = new PromEnrollmentResponse(secondEnrollmentId, mainPersonResponse, null,
            false, "message")

    @Subject
    private PromAdminController promAdminController

    private PromAdminServiceImpl promAdminService
    private MockMvc mockMvc

    def setup() {
        promAdminService = Mock()
        promAdminController = new PromAdminController(promAdminService)
        mockMvc = MockMvcBuilders.standaloneSetup(promAdminController)
                                 .setControllerAdvice(new RestExceptionHandler())
                                 .build()
    }

    def "should return all enrollments which belongs to user"() {
        when:
            def response = mockMvc.perform(get(BASE_URL)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.getAllEnrollments() >> [pairResponse, singleResponse]
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.[0].promEnrollmentId').value(pairResponse.getPromEnrollmentId().toString()))
            response.andExpect(jsonPath('$.[0].mainPerson').value(pairResponse.getMainPerson()))
            response.andExpect(jsonPath('$.[0].partner').value(pairResponse.getPartner()))
            response.andExpect(jsonPath('$.[0].paid').value(pairResponse.isPaid()))
            response.andExpect(jsonPath('$.[0].message').value(pairResponse.getMessage()))
            response.andExpect(jsonPath('$.[1].promEnrollmentId').value(singleResponse.getPromEnrollmentId().toString()))
            response.andExpect(jsonPath('$.[1].mainPerson').value(singleResponse.getMainPerson()))
            response.andExpect(jsonPath('$.[1].partner').value(null))
            response.andExpect(jsonPath('$.[1].paid').value(singleResponse.isPaid()))
            response.andExpect(jsonPath('$.[1].message').value(singleResponse.getMessage()))
            response.andExpect(jsonPath('$.[2]').doesNotExist())
    }

    def "should return empty list when get prom enrollments but any exist"() {
        when:
            def response = mockMvc.perform(get(BASE_URL)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.getAllEnrollments() >> []
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.[0]').doesNotExist())
    }

    def "should return enrollment"() {
        when:
            def response = mockMvc.perform(get(String.format(URL_WITH_ID), enrollmentId)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.getEnrollment(enrollmentId) >> pairResponse
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.promEnrollmentId').value(pairResponse.getPromEnrollmentId().toString()))
            response.andExpect(jsonPath('$.mainPerson').value(pairResponse.getMainPerson()))
            response.andExpect(jsonPath('$.partner').value(pairResponse.getPartner()))
            response.andExpect(jsonPath('$.paid').value(pairResponse.isPaid()))
            response.andExpect(jsonPath('$.message').value(pairResponse.getMessage()))
    }

    def "should return error response when user try to get enrollment which does not exist"() {
        when:
            def response = mockMvc.perform(get(String.format(URL_WITH_ID), enrollmentId)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.getEnrollment(enrollmentId) >>
                    {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should update mainPerson data and response with 200"() {
        given:
            PromEnrollmentPersonRequest personToUpdate = new PromEnrollmentPersonRequest("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
            PromEnrollmentPersonResponse updatedPerson = new PromEnrollmentPersonResponse("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "?person=mainPerson"), enrollmentId)
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.updateEnrollment(personToUpdate, enrollmentId, "mainPerson") >> updatedPerson
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.name').value(updatedPerson.getName()))
            response.andExpect(jsonPath('$.surname').value(updatedPerson.getSurname()))
            response.andExpect(jsonPath('$.email').value(updatedPerson.getEmail()))
            response.andExpect(jsonPath('$.phoneNumber').value(updatedPerson.getPhoneNumber()))
    }

    @Unroll
    def "should respond with 400 and custom error message when request is not valid when update"() {
        given:
            Map person = [
                name: name,
                surname: surname,
                email: email,
                phoneNumber: phoneNumber,
                fromAGH: fromAGH,
                index: null,
                faculty: null,
                field: null,
                year: null
            ]
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "?person=mainPerson"), enrollmentId)
                                  .content(JsonOutput.toJson(person))
                                  .contentType(APPLICATION_JSON))
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value(errorMessage))
        where:
            name        | surname        | email            | phoneNumber | fromAGH | errorMessage
            null        | "firstSurname" | "test@gmail.com" | "123456789" | false   | "name: Name field can't be blank"
            " "         | "firstSurname" | "test@gmail.com" | "123456789" | false   | "name: Name field can't be blank"
            "firstName" | null           | "test@gmail.com" | "123456789" | false   | "surname: Surname field can't be blank"
            "firstName" | " "            | "test@gmail.com" | "123456789" | false   | "surname: Surname field can't be blank"
            "firstName" | "firstSurname" | null             | "123456789" | false   | "email: Email field can't be blank"
            "firstName" | "firstSurname" | ""               | "123456789" | false   | "email: Email field can't be blank"
            "firstName" | "firstSurname" | "badValue"       | "123456789" | false   | "email: Wrong value for email field. You need to provide email address"
            "firstName" | "firstSurname" | "test@gmail.com" | null        | false   | "phoneNumber: Phone number field can't be blank"
            "firstName" | "firstSurname" | "test@gmail.com" | ""          | false   | "phoneNumber: Wrong value for phone number field. You need to provide valid phone number"
            "firstName" | "firstSurname" | "test@gmail.com" | "badValue"  | false   | "phoneNumber: Wrong value for phone number field. You need to provide valid phone number"
    }

    def "should return with 400 and custom exception when updating person is not possible because wrong path parameter provided"() {
        given:
            PromEnrollmentPersonRequest personToUpdate = new PromEnrollmentPersonRequest("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "?person=wrongValue"), enrollmentId)
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.updateEnrollment(personToUpdate, enrollmentId, "wrongValue") >>
                    {throw new PromPersonEntityNotExistException("Person to update does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Person to update does not exist"))
    }

    def "should return with 400 and custom exception when enrollment with given id does not exist"() {
        given:
            PromEnrollmentPersonRequest personToUpdate = new PromEnrollmentPersonRequest("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "?person=mainPerson"), enrollmentId)
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.updateEnrollment(personToUpdate, enrollmentId, "mainPerson") >>
                {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should delete enrollment"() {
        when:
            def response = mockMvc.perform(delete(String.format(URL_WITH_ID), enrollmentId)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.deleteEnrollment(enrollmentId)
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.action').value("Enrollment successfully deleted"))
    }

    def "should return error response when user try to delete enrollment which does not exist"() {
        when:
            def response = mockMvc.perform(delete(String.format(URL_WITH_ID), enrollmentId)
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.deleteEnrollment(enrollmentId) >>
                {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should change paid status of enrollment"() {
        given:
            PaidRequest paidRequest = new PaidRequest(true)
            PromEnrollmentResponse singleResponse = new PromEnrollmentResponse(secondEnrollmentId, mainPersonResponse, null,
                    true, "message")
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "/paid"), enrollmentId)
                                  .content(JsonOutput.toJson(paidRequest))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.changeEnrollmentPaidStatus(enrollmentId, paidRequest) >> singleResponse
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.promEnrollmentId').value(singleResponse.getPromEnrollmentId().toString()))
            response.andExpect(jsonPath('$.mainPerson').value(singleResponse.getMainPerson()))
            response.andExpect(jsonPath('$.partner').value(singleResponse.getPartner()))
            response.andExpect(jsonPath('$.paid').value(true))
            response.andExpect(jsonPath('$.message').value(singleResponse.getMessage()))
    }

    def "should return error response when user try to change paid status of enrollment which does not exist"() {
        given:
            PaidRequest paidRequest = new PaidRequest(true)
        when:
            def response = mockMvc.perform(put(String.format(URL_WITH_ID + "/paid"), enrollmentId)
                                  .content(JsonOutput.toJson(paidRequest))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promAdminService.changeEnrollmentPaidStatus(enrollmentId, paidRequest) >>
                    {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }
}