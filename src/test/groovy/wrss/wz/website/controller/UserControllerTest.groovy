package wrss.wz.website.controller

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification;
import spock.lang.Subject
import spock.lang.Unroll
import wrss.wz.website.exception.RestExceptionHandler
import wrss.wz.website.exception.custom.UserAlreadyExistException
import wrss.wz.website.model.request.UserRequest
import wrss.wz.website.model.response.UserResponse
import wrss.wz.website.service.UserServiceImpl;
import groovy.json.JsonOutput

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.http.MediaType.APPLICATION_JSON

class UserControllerTest extends Specification {

    @Shared
    String NAME = "TestUser"

    @Shared
    String USERNAME = "user@gmail.com"

    @Shared
    String PASSWORD = "password"

    String URL = "/api/user"
    String ERROR_TYPE = "InvalidRequestArgumentValue"
    UUID USER_ID = UUID.randomUUID()

    @Subject
    private UserController userController

    private UserServiceImpl userService
    private MockMvc mockMvc

    def setup() {
        userService = Mock()
        userController = new UserController(userService)
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                                 .setControllerAdvice(new RestExceptionHandler())
                                 .build()
    }

    def "should create new user"() {
        given:
            Map request = [
                    name: name,
                    username: username,
                    password: password
            ]
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(request))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * userService.createUser(new UserRequest(NAME, USERNAME, PASSWORD)) >> new UserResponse(USERNAME, USER_ID)
        then:
            response.andExpect(status().isOk())
            response.andExpect(jsonPath('$.username').value(USERNAME))
            response.andExpect(jsonPath('$.userId').value(USER_ID.toString()))
        where:
            name | username | password
            NAME | USERNAME | PASSWORD
    }

    def "should return 400 and custom exception when user already exist in database"() {
        given:
            Map request = [
                    name: name,
                    username: username,
                    password: password
            ]
        when:
            def response = mockMvc.perform(post(URL)
                                  .content(JsonOutput.toJson(request))
                                  .contentType(APPLICATION_JSON))
        then:
            1 * userService.createUser(new UserRequest(NAME, USERNAME, PASSWORD)) >>
                    {throw new UserAlreadyExistException("username: User with this email already exist")}
        then:
            response.andExpect(status().isBadRequest())
            response.andExpect(jsonPath('$.errorType').value(ERROR_TYPE))
            response.andExpect(jsonPath('$.errorMessage[0]').value(errorMessage))
        where:
            name | username  | password | errorMessage
            NAME | USERNAME  | PASSWORD | "username: User with this email already exist"
    }

    @Unroll
    def "should return 400 and custom exception when create user request is not valid"() {
        given:
            Map request = [
                    name: name,
                    username: username,
                    password: password
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
            name | username  | password | errorMessage
            null | USERNAME  | PASSWORD | "name: Name field can't be blank"
            " "  | USERNAME  | PASSWORD | "name: Name field can't be blank"
            NAME | "noEmail" | PASSWORD | "username: Wrong value for username field. You need to provide email address"
            NAME | null      | PASSWORD | "username: Username field can't be blank"
            NAME | ""        | PASSWORD | "username: Username field can't be blank"
            NAME | USERNAME  | null     | "password: Password field can't be blank"
            NAME | USERNAME  | " "      | "password: Password field can't be blank"
    }
}
