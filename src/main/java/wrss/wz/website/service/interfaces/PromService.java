package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromGetEnrollmentResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface PromService {
    List<PromGetEnrollmentResponse> getAll(String username);
    PromGetEnrollmentResponse get(UUID enrollmentId, String username);
    PromEnrollmentResponse signUp(PromEnrollmentRequest promEnrollmentRequest, String username);
    void update(PromEnrollmentPersonRequest promEnrollmentRequest, UUID enrollmentId, String person, String username);
}