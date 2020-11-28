package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.exception.custom.UserAlreadyExistException;
import wrss.wz.website.model.request.UserRequest;
import wrss.wz.website.model.response.UserResponse;
import wrss.wz.website.repository.RoleRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.security.UserDetailsImpl;
import wrss.wz.website.service.interfaces.UserService;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest userRequest) {

        if (userRepository.findByUsername(userRequest.getUsername()) != null) {
            throw new UserAlreadyExistException("username: User with this email already exist");
        }

        StudentEntity studentEntity = modelMapper.map(userRequest, StudentEntity.class);
        studentEntity.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
        studentEntity.setUserId(UUID.randomUUID().toString());
        studentEntity.addRole(roleRepository.findByRole("STUDENT"));

        StudentEntity savedUser = userRepository.save(studentEntity);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StudentEntity user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("Could not find user with username %s", username));
        }

        return new UserDetailsImpl(user);
    }
}
