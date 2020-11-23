package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PromSignUpRequest;
import wrss.wz.website.model.response.PromSignUpResponse;

public interface PromService {
    PromSignUpResponse signUp(PromSignUpRequest promSignUpRequest, String username);
}