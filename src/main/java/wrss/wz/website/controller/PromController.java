package wrss.wz.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.model.request.PromEnrollmentPersonRequest;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.DefaultResponse;
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.service.interfaces.PromService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/student/prom")
@RequiredArgsConstructor
public class PromController {

    public final PromService promService;

    @GetMapping
    public List<PromEnrollmentResponse> getAll(HttpServletRequest request) {

        String username = request.getAttribute("username").toString();
        return promService.getAll(username);
    }

    @GetMapping("/{enrollmentId}")
    public PromEnrollmentResponse get(@PathVariable UUID enrollmentId, HttpServletRequest request) {

        String username = request.getAttribute("username").toString();
        return promService.get(enrollmentId, username);
    }

    @PostMapping
    public PromEnrollmentResponse signUp(@Valid @RequestBody PromEnrollmentRequest promEnrollmentRequest,
                                         HttpServletRequest request) {

        String username = request.getAttribute("username").toString();
        return promService.signUp(promEnrollmentRequest, username);
    }

    @PutMapping("/{enrollmentId}/{person}")
    public PromEnrollmentPersonResponse update(@Valid @RequestBody PromEnrollmentPersonRequest promEnrollmentPersonRequest,
                                               @PathVariable UUID enrollmentId, @PathVariable String person,
                                               HttpServletRequest request) {

        String username = request.getAttribute("username").toString();
        return promService.update(promEnrollmentPersonRequest, enrollmentId, person, username);
    }

    @PutMapping("/{enrollmentId}/transfer")
    public DefaultResponse transfer(@PathVariable UUID enrollmentId, @RequestParam String newUsername,
                                    HttpServletRequest request) {

        String username = request.getAttribute("username").toString();
        promService.transfer(enrollmentId, newUsername, username);

        return new DefaultResponse(String.format("Enrollment successfully transferred to %s", newUsername));
    }
}