package wrss.wz.website.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wrss.wz.website.dto.UserDto;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.repository.RoleRepository;
import wrss.wz.website.repository.UserRepository;
import wrss.wz.website.security.UserDetailsImpl;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private static final ModelMapper modelMapper = new ModelMapper();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        //TODO: Create custom exception
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            throw new RuntimeException();
        }

        StudentEntity studentEntity = modelMapper.map(userDto, StudentEntity.class);

        studentEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        studentEntity.setUserId(UUID.randomUUID().toString());
        studentEntity.addRole(roleRepository.findByRole("STUDENT"));

        StudentEntity savedUser = userRepository.save(studentEntity);

        return modelMapper.map(savedUser, UserDto.class);
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
