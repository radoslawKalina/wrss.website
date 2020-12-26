package wrss.wz.website.service.interfaces;

import wrss.wz.website.model.request.PaidRequest;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface PromAdminService {

    List<PromEnrollmentResponse> getAllEnrollments();
    PromEnrollmentResponse getEnrollment(UUID enrollmentId);
    PromEnrollmentPersonResponse updateEnrollment(PromEnrollmentPersonRequest promEnrollmentPersonRequest, UUID enrollmentId, String person);
    void deleteEnrollment(UUID enrollmentId);
    PromEnrollmentResponse changeEnrollmentPaidStatus(UUID enrollmentId, PaidRequest paidRequest);
}
