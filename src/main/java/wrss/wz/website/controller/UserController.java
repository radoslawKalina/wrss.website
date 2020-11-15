package wrss.wz.website.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.model.request.UserRequest;
import wrss.wz.website.model.response.UserResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return null;
    }
}
