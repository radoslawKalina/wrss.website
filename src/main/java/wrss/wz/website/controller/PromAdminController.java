package wrss.wz.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.model.request.PaidRequest;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.response.DefaultResponse;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.service.interfaces.PromAdminService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin/prom")
@RequiredArgsConstructor
public class PromAdminController {

    private final PromAdminService promAdminService;

    @GetMapping
    public List<PromEnrollmentResponse> getAllEnrollments() {

        return promAdminService.getAllEnrollments();
    }

    @GetMapping("/{enrollmentId}")
    public PromEnrollmentResponse getEnrollment(@PathVariable UUID enrollmentId) {

        return promAdminService.getEnrollment(enrollmentId);
    }

    @PutMapping("/{enrollmentId}")
    public PromEnrollmentPersonResponse updateEnrollment(@Valid @RequestBody PromEnrollmentPersonRequest promEnrollmentPersonRequest,
                                                         @PathVariable UUID enrollmentId, @RequestParam String person) {

        return promAdminService.updateEnrollment(promEnrollmentPersonRequest, enrollmentId, person);
    }

    @DeleteMapping("/{enrollmentId}")
    public DefaultResponse deleteEnrollment(@PathVariable UUID enrollmentId) {

        promAdminService.deleteEnrollment(enrollmentId);
        return new DefaultResponse("Enrollment successfully deleted");
    }

    @PutMapping("/{enrollmentId}/paid")
    public PromEnrollmentResponse changeEnrollmentPaidStatus(@RequestBody PaidRequest paidRequest, @PathVariable UUID enrollmentId) {

        return promAdminService.changeEnrollmentPaidStatus(enrollmentId, paidRequest);
    }
}
