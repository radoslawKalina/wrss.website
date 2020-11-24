package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromGetEnrollmentResponse;
import wrss.wz.website.model.response.PromSignUpResponse;

import java.util.List;

public interface PromService {
    List<PromGetEnrollmentResponse> getAll(String username);
    PromSignUpResponse signUp(PromEnrollmentRequest promEnrollmentRequest, String username);
}