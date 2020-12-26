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
import wrss.wz.website.exception.custom.RecordBelongingException
import wrss.wz.website.exception.custom.UserDoesNotExistException
import wrss.wz.website.model.request.PromEnrollmentPersonRequest
import wrss.wz.website.model.request.PromEnrollmentRequest
import wrss.wz.website.model.response.PromEnrollmentPersonResponse
import wrss.wz.website.model.response.PromEnrollmentResponse
import wrss.wz.website.service.PromServiceImpl

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PromControllerTest extends Specification {

    String URL = "/api/student/prom"

    UUID enrollmentId = UUID.randomUUID()
    UUID secondEnrollmentId = UUID.randomUUID()

    PromEnrollmentPersonRequest mainPerson = new PromEnrollmentPersonRequest("firstName", "firstSurname",
            "first@gmail.com", "123456789", true, 284242, "WZ", "IiE", 2)

    PromEnrollmentPersonRequest partner = new PromEnrollmentPersonRequest("secondName", "secondSurname",
            "second@gmail.com", "234567890", false, null, null, null, null)

    PromEnrollmentRequest pairRequest = new PromEnrollmentRequest("pair", mainPerson, partner, "message")
    PromEnrollmentRequest singleRequest = new PromEnrollmentRequest("single", mainPerson, null, "message")

    PromEnrollmentPersonResponse mainPersonResponse = new PromEnrollmentPersonResponse("firstName", "firstSurname",
            "first@gmail.com", "123456789", true, 284242, "WZ", "IiE", 2)

    PromEnrollmentPersonResponse partnerResponse = new PromEnrollmentPersonResponse("secondName", "secondSurname",
            "second@gmail.com", "234567890", false, null, null, null, null)

    PromEnrollmentResponse pairResponse = new PromEnrollmentResponse(enrollmentId, mainPersonResponse, partnerResponse,
            false, "message")
    PromEnrollmentResponse singleResponse = new PromEnrollmentResponse(secondEnrollmentId, mainPersonResponse, null,
            false, "message")

    @Subject
    private PromController promController

    private PromServiceImpl promService
    private MockMvc mockMvc

    def setup() {
        promService = Mock()
        promController = new PromController(promService)
        mockMvc = MockMvcBuilders.standaloneSetup(promController)
                                 .setControllerAdvice(new RestExceptionHandler())
                                 .build()
    }

    def "should return all enrollments which belongs to user"() {
        when:
            def response = mockMvc.perform(get(URL)
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.getOwnEnrollments("test@gmail.com") >> [pairResponse, singleResponse]
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

    def "should return empty list when get prom enrollments but user do not have any"() {
        when:
            def response = mockMvc.perform(get(URL)
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.getOwnEnrollments("test@gmail.com") >> []
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.[0]').doesNotExist())
    }

    def "should return enrollment when it belongs to user"() {
        when:
            def response = mockMvc.perform(get(String.format("%s/%s", URL, enrollmentId.toString()))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.getEnrollment(enrollmentId, "test@gmail.com") >> pairResponse
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
        def response = mockMvc.perform(get(String.format("%s/%s", URL, enrollmentId.toString()))
                              .requestAttr("username", "test@gmail.com")
                              .contentType(APPLICATION_JSON))
        then:
            1 * promService.getEnrollment(enrollmentId, "test@gmail.com") >>
                    {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
        response.andExpect(status().isBadRequest())
        response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
        response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should return error response when user try to get enrollment which not belong to him"() {
        when:
            def response = mockMvc.perform(get(String.format("%s/%s", URL, enrollmentId.toString()))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.getEnrollment(enrollmentId, "test@gmail.com") >>
                    {throw new RecordBelongingException("This record belong to another user")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("OperationNotAllowedForUser"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("This record belong to another user"))
    }

    def "should create prom enrollment pair type and return with 200"() {
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(pairRequest))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.createEnrollment(pairRequest, "test@gmail.com") >> pairResponse
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.promEnrollmentId').value(enrollmentId.toString()))
            response.andExpect(jsonPath('$.paid').value("false"))
            response.andExpect(jsonPath('$.message').value("message"))
    }

    def "should create prom enrollment single type and return with 200"() {
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(singleRequest))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.createEnrollment(singleRequest, "test@gmail.com") >> singleResponse
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.promEnrollmentId').value(secondEnrollmentId.toString()))
            response.andExpect(jsonPath('$.paid').value("false"))
            response.andExpect(jsonPath('$.message').value("message"))
    }

    def "should return 400 and custom exception when mainPerson is null during sign up pair type"() {
        given:
            Map partner = [
                name: pName,
                surname: pSurname,
                email: pEmail,
                phoneNumber: pPhoneNumber,
                fromAGH: pFromAGH,
                index: pIndex,
                faculty: pFaculty,
                firld: pField,
                year: pYear
            ]
            Map request = [
                type: "pair",
                mainPerson: null,
                partner: partner,
                message: "message"
            ]
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(request))
                                  .contentType(APPLICATION_JSON))
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("mainPerson: Missing mainPerson field"))
        where:
            pName         | pSurname         | pEmail             | pPhoneNumber | pFromAGH | pIndex   | pFaculty | pField | pYear
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5
    }

    def "should return 400 and custom exception when mainPerson is null during sign up single type"() {
        given:
            Map request = [
                type: "single",
                mainPerson: null,
                partner: null,
                message: "message"
            ]
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(request))
                                  .contentType(APPLICATION_JSON))
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("mainPerson: Missing mainPerson field"))
    }

    @Unroll
    def "should return 400 and custom exception when request is not valid during sign up"() {
        given:
            Map mainPerson = [
                    name: name,
                    surname: surname,
                    email: email,
                    phoneNumber: phoneNumber,
                    fromAGH: fromAGH,
                    index: index,
                    faculty: faculty,
                    field: field,
                    year: year
            ]
            Map partner = [
                    name: pName,
                    surname: pSurname,
                    email: pEmail,
                    phoneNumber: pPhoneNumber,
                    fromAGH: pFromAGH,
                    index: pIndex,
                    faculty: pFaculty,
                    firld: pField,
                    year: pYear
            ]
            Map request = [
                    type: type,
                    mainPerson: mainPerson,
                    partner: partner,
                    message: message
            ]
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(request))
                                  .contentType(APPLICATION_JSON))
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value(errorMessage))
        where:
            name          | surname          | email              | phoneNumber  | fromAGH  | index    | faculty  | field  | year  |
            pName         | pSurname         | pEmail             | pPhoneNumber | pFromAGH | pIndex   | pFaculty | pField | pYear |
            type          | message          | errorMessage

            // [0] type = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            null          | "message"        | "type: Missing type field. You need to specify is it a single person or pair enrollment"

            // [1] type = ""
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            ""            | "message"        | "type: Wrong value for type field. Use single or pair"

            // [2] type = "badValue"
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "badValue"    | "message"        | "type: Wrong value for type field. Use single or pair"

            // [3] mainPerson.name = null
            null          | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.name: Name field can't be blank"

            // [4] mainPerson.name = " "
            " "           | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.name: Name field can't be blank"

            // [5] mainPerson.surname = null
            "firstName"   | null             | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.surname: Surname field can't be blank"

            // [6] mainPerson.surname = " "
            "firstName"   | " "              | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.surname: Surname field can't be blank"

            // [7] mainPerson.email = null
            "firstName"   | "firstSurname"   | null               | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.email: Email field can't be blank"

            // [8] mainPerson.email = ""
            "firstName"   | "firstSurname"   | ""                 | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.email: Email field can't be blank"

            // [9] mainPerson.email = "badValue"
            "firstName"   | "firstSurname"   | "badValue"         | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.email: Wrong value for email field. You need to provide email address"

            // [10] mainPerson.phoneNumber = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | null         | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.phoneNumber: Phone number field can't be blank"

            // [11] mainPerson.phoneNumber = ""
            "firstName"   | "firstSurname"   | "first@gmail.com"  | ""           | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.phoneNumber: Wrong value for phone number field. You need to provide valid phone number"

            // [12] mainPerson.phoneNumber = "badValue"
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "badValue"   | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "mainPerson.phoneNumber: Wrong value for phone number field. You need to provide valid phone number"

            // [13] partner.name = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            null          | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.name: Name field can't be blank"

            // [14] partner.name = " "
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            " "           | "partnerSurname" | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.name: Name field can't be blank"

            // [15] partner.surname = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | null             | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.surname: Surname field can't be blank"

            // [16] partner.surname = " "
            "firstName"   | "partnerSurname" | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | " "              | "second@gmail.com" | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.surname: Surname field can't be blank"

            // [17] partner.email = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | null               | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.email: Email field can't be blank"

            // [18] partner.email = ""
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | ""                 | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.email: Email field can't be blank"

            // [19] partner.email = "badValue"
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "badValue"         | "234567890"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.email: Wrong value for email field. You need to provide email address"

            // [20] partner.phoneNumber = null
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | null         | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.phoneNumber: Phone number field can't be blank"

            // [21] partner.phoneNumber = ""
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | ""           | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.phoneNumber: Wrong value for phone number field. You need to provide valid phone number"

            // [22] partner.phoneNumber = "badValue"
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"  | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "badValue"   | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.phoneNumber: Wrong value for phone number field. You need to provide valid phone number"
    }

    def "should update mainPerson data and response with 200"() {
        given:
            PromEnrollmentPersonRequest personToUpdate = new PromEnrollmentPersonRequest("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
            PromEnrollmentPersonResponse updatedPerson = new PromEnrollmentPersonResponse("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/?person=mainPerson", URL, enrollmentId.toString()))
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.updateEnrollment(personToUpdate, enrollmentId, "mainPerson", "test@gmail.com") >> updatedPerson
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
            def response = mockMvc.perform(put(String.format("%s/%s/?person=mainPerson", URL, enrollmentId.toString()))
                                  .content(JsonOutput.toJson(person))
                                  .requestAttr("username", "test@gmail.com")
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
            def response = mockMvc.perform(put(String.format("%s/%s/?person=wrongValue", URL, enrollmentId.toString()))
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.updateEnrollment(personToUpdate, enrollmentId, "wrongValue", "test@gmail.com") >>
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
            def response = mockMvc.perform(put(String.format("%s/%s/?person=mainPerson", URL, enrollmentId.toString()))
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.updateEnrollment(personToUpdate, enrollmentId, "mainPerson", "test@gmail.com") >>
                {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should return with 400 and custom exception when user try to update enrollment which not belongs to him"() {
        given:
            PromEnrollmentPersonRequest personToUpdate = new PromEnrollmentPersonRequest("updatedName", "updatedSurname",
                "updated@gmail.com", "345678901", true, 294242, "WIMiR", "IMiM", 3)
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/?person=mainPerson", URL, secondEnrollmentId.toString()))
                                  .content(JsonOutput.toJson(personToUpdate))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.updateEnrollment(personToUpdate, secondEnrollmentId, "mainPerson", "test@gmail.com") >>
                {throw new RecordBelongingException("This record belong to another user")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("OperationNotAllowedForUser"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("This record belong to another user"))
    }

    def "should transfer enrollment to another person"() {
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/transfer/?newUsername=%s", URL, enrollmentId.toString(), "new@gmail.com"))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.transferEnrollment(enrollmentId, "new@gmail.com", "test@gmail.com")
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.action').value("Enrollment successfully transferred to new@gmail.com"))
    }

    def "should return with 400 and custom exception when user to whom enrollment transfer targeting does not exist"() {
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/transfer/?newUsername=%s", URL, enrollmentId.toString(), "new@gmail.com"))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.transferEnrollment(enrollmentId, "new@gmail.com", "test@gmail.com") >>
                    {throw new UserDoesNotExistException("User to whom you try to transfer enrollment does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("User to whom you try to transfer enrollment does not exist"))
    }

    def "should return with 400 and custom exception when user try to transfer enrollment which does not exist"() {
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/transfer/?newUsername=%s", URL, enrollmentId.toString(), "new@gmail.com"))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.transferEnrollment(enrollmentId, "new@gmail.com", "test@gmail.com") >>
                {throw new PromEnrollmentDoesNotExist("Prom enrollment with this Id does not exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value("InvalidRequestArgumentValue"))
            response.andExpect(jsonPath('$.errorMessage[0]').value("Prom enrollment with this Id does not exist"))
    }

    def "should return with 400 and custom exception when user try to transfer enrollment which does not belong to him"() {
        when:
            def response = mockMvc.perform(put(String.format("%s/%s/transfer/?newUsername=%s", URL, enrollmentId.toString(), "new@gmail.com"))
                                  .requestAttr("username", "test@gmail.com")
                                  .contentType(APPLICATION_JSON))
        then:
            1 * promService.transferEnrollment(enrollmentId, "new@gmail.com", "test@gmail.com") >>
                {throw new RecordBelongingException("This record belong to another user")}
        then:
        response.andExpect(status().isBadRequest())
        response.andExpect(jsonPath('$.errorType').value("OperationNotAllowedForUser"))
        response.andExpect(jsonPath('$.errorMessage[0]').value("This record belong to another user"))
    }
}