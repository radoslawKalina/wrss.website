package wrss.wz.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.model.request.PromEnrollmentRequest;
import wrss.wz.website.model.response.PromGetEnrollmentResponse;
import wrss.wz.website.model.response.PromSignUpResponse;
import wrss.wz.website.service.interfaces.PromService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/student/prom")
@RequiredArgsConstructor
public class PromController {

    public final PromService promService;

    @GetMapping
    public List<PromGetEnrollmentResponse> getAll(Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.getAll(username);
    }

    @PostMapping
    public PromSignUpResponse signUp(@Valid @RequestBody PromEnrollmentRequest promEnrollmentRequest, Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.signUp(promEnrollmentRequest, username);
    }
}