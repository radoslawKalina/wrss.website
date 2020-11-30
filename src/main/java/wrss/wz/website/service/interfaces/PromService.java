package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface PromService {
    List<PromEnrollmentResponse> getAll(String username);
    PromEnrollmentResponse get(UUID enrollmentId, String username);
    PromEnrollmentResponse signUp(PromEnrollmentRequest promEnrollmentRequest, String username);
    PromEnrollmentPersonResponse update(PromEnrollmentPersonRequest promEnrollmentRequest, UUID enrollmentId,
                                        String person, String username);
}