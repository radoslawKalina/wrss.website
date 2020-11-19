package wrss.wz.website.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wrss.wz.website.dto.UserDto;
import wrss.wz.website.entity.StudentEntity;
import wrss.wz.website.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final ModelMapper modelMapper = new ModelMapper();

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        //TODO: Add checking for users already existing in database
        //TODO: Add user roles

        StudentEntity studentEntity = modelMapper.map(userDto, StudentEntity.class);
        studentEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        studentEntity.setUserId(UUID.randomUUID().toString());

        StudentEntity savedUser = userRepository.save(studentEntity);

        return modelMapper.map(savedUser, UserDto.class);
    }
}