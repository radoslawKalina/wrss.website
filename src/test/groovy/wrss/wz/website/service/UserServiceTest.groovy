package wrss.wz.website.service

import org.modelmapper.ModelMapper
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification
import spock.lang.Subject
import wrss.wz.website.entity.StudentEntity
import wrss.wz.website.exception.custom.UserAlreadyExistException
import wrss.wz.website.model.request.UserRequest
import wrss.wz.website.repository.RoleRepository
import wrss.wz.website.repository.UserRepository

class UserServiceTest extends Specification {

    @Subject
    private UserServiceImpl userServiceImpl

    private UserRepository userRepository
    private RoleRepository roleRepository
    private ModelMapper modelMapper
    private BCryptPasswordEncoder bCryptPasswordEncoder

    def setup() {
        userRepository = Mock()
        roleRepository = Mock()
        modelMapper = Mock()
        bCryptPasswordEncoder = Mock()

        userServiceImpl = new UserServiceImpl(modelMapper, userRepository, roleRepository, bCryptPasswordEncoder)
    }

    def "should throw UserAlreadyExistException when provided email already exist in database"() {
        given:
            UserRequest userRequest = new UserRequest("TestUser", "user@gmail.com", "password")
        when:
            userServiceImpl.createUser(userRequest)
        then:
            1 * userRepository.findByUsername(userRequest.getUsername()) >> new StudentEntity()
        then:
            thrown(UserAlreadyExistException)
    }
}
