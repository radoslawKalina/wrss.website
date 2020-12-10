package wrss.wz.website.controller

import groovy.json.JsonOutput
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import wrss.wz.website.exception.RestExceptionHandler
import wrss.wz.website.service.PromServiceImpl

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class PromControllerTest extends Specification {

    String URL = "/api/student/prom"
    String ERROR_TYPE = "InvalidRequestArgumentValue"

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
            response.andExpect(jsonPath('$.errorType').value(ERROR_TYPE))
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
            response.andExpect(jsonPath('$.errorType').value(ERROR_TYPE))
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
            response.andExpect(jsonPath('$.errorType').value(ERROR_TYPE))
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
            "firstName"   | "firstSurname"   | "first@gmail.com"  | "123456789"   | true     | "284277" | "WZ"     | "IiE"  | 2     |
            "partnerName" | "partnerSurname" | "second@gmail.com" | "badValue"  | true     | "284255" | "WZ"     | "ZiIP" | 5     |
            "pair"        | "message"        | "partner.phoneNumber: Wrong value for phone number field. You need to provide valid phone number"
    }

    /*
    PromEnrollmentPersonResponse mainPersonResponse = new PromEnrollmentPersonResponse(mainPerson.name as String,
        mainPerson.surname as String, mainPerson.email as String as String, mainPerson.phoneNumber as String,
        mainPerson.fromAGH as boolean, mainPerson.index as Integer as Integer, mainPerson.faculty as String,
        mainPerson.field as String, mainPerson.year as Integer)

    PromEnrollmentPersonResponse partnerResponse = new PromEnrollmentPersonResponse(partner.name as String,
        partner.surname as String, partner.email as String as String, partner.phoneNumber as String,
        partner.fromAGH as boolean, partner.index as Integer as Integer, partner.faculty as String,
        partner.field as String, partner.year as Integer)

    PromEnrollmentResponse serviceResponse = new PromEnrollmentResponse(PROM_ID, mainPersonResponse, partnerResponse,
        false, "message")
    */
}