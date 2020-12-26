package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface PromService {
    List<PromEnrollmentResponse> getOwnEnrollments(String username);
    PromEnrollmentResponse getEnrollment(UUID enrollmentId, String username);
    PromEnrollmentResponse createEnrollment(PromEnrollmentRequest promEnrollmentRequest, String username);
    PromEnrollmentPersonResponse updateEnrollment(PromEnrollmentPersonRequest promEnrollmentRequest, UUID enrollmentId,
                                                  String person, String username);
    void transferEnrollment(UUID enrollmentId, String newUsername, String username);
}