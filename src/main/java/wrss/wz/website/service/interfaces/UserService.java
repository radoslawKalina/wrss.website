package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.UserRequest;
import wrss.wz.website.model.response.UserResponse;

public interface UserService {
    
    UserResponse createUser(UserRequest userRequest);
}