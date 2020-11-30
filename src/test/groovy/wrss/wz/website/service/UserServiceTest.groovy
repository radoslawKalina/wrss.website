package wrss.wz.website.service

import org.modelmapper.ModelMapper
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import wrss.wz.website.entity.StudentEntity
import wrss.wz.website.exception.custom.UserAlreadyExistException
import wrss.wz.website.model.request.UserRequest
import wrss.wz.website.model.response.UserResponse
import wrss.wz.website.repository.RoleRepository
import wrss.wz.website.repository.UserRepository

class UserServiceTest extends Specification {

    @Shared
    String NAME = "TestUser"
    String USERNAME = "user@gmail.com"
    String PASSWORD = "password"
    UUID USER_ID = UUID.randomUUID()

    UserRequest userRequest = new UserRequest(NAME, USERNAME, PASSWORD)

    @Subject
    private UserServiceImpl userServiceImpl

    private UserRepository userRepository
    private RoleRepository roleRepository
    private ModelMapper modelMapper
    private BCryptPasswordEncoder bCryptPasswordEncoder

    def setup() {
        userRepository = Mock()
        roleRepository = Mock()
        modelMapper = new ModelMapper()
        bCryptPasswordEncoder = new BCryptPasswordEncoder()

        userServiceImpl = new UserServiceImpl(modelMapper, userRepository, roleRepository, bCryptPasswordEncoder)
    }

    def "should create user and return UserResponse"() {
        given:
            StudentEntity studentEntity = new StudentEntity()
            studentEntity.setName(NAME)
            studentEntity.setUsername(USERNAME)
            studentEntity.setPassword(bCryptPasswordEncoder.encode(PASSWORD))
            studentEntity.setUserId(USER_ID)
        when:
            UserResponse userResponse = userServiceImpl.createUser(userRequest)
        then:
            1 * userRepository.findByUsername(userRequest.getUsername()) >> null
            1 * userRepository.save(_ as StudentEntity) >> studentEntity
        then:
            userResponse.getUsername() == USERNAME
            userResponse.getUserId() == USER_ID
    }

    def "should throw UserAlreadyExistException when provided email already exist in database"() {
        when:
            userServiceImpl.createUser(userRequest)
        then:
            1 * userRepository.findByUsername(userRequest.getUsername()) >> new StudentEntity()
        then:
            thrown(UserAlreadyExistException)
    }
}
