package wrss.wz.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
import wrss.wz.website.model.response.PromEnrollmentPersonResponse;
import wrss.wz.website.model.response.PromEnrollmentResponse;
import wrss.wz.website.service.interfaces.PromService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/student/prom")
@RequiredArgsConstructor
public class PromController {

    public final PromService promService;

    @GetMapping
    public List<PromEnrollmentResponse> getAll(Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.getAll(username);
    }

    @GetMapping("/{enrollmentId}")
    public PromEnrollmentResponse get(@PathVariable UUID enrollmentId, Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.get(enrollmentId, username);
    }

    @PostMapping
    public PromEnrollmentResponse signUp(@Valid @RequestBody PromEnrollmentRequest promEnrollmentRequest, Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.signUp(promEnrollmentRequest, username);
    }

    @PutMapping("/{enrollmentId}")
    public PromEnrollmentPersonResponse update(@Valid @RequestBody PromEnrollmentPersonRequest promEnrollmentPersonRequest,
                                               @PathVariable UUID enrollmentId, @RequestParam String person, Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.update(promEnrollmentPersonRequest, enrollmentId, person, username);
    }
}