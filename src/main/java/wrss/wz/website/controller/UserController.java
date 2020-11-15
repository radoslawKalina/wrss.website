package wrss.wz.website.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.service.UserService;
import wrss.wz.website.dto.UserDto;
import wrss.wz.website.model.request.UserRequest;
import wrss.wz.website.model.response.UserResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final ModelMapper modelMapper = new ModelMapper();

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {

        UserDto requestUser = modelMapper.map(userRequest, UserDto.class);

        UserDto createdUser = userService.createUser(requestUser);

        return modelMapper.map(createdUser, UserResponse.class);
    }
}
