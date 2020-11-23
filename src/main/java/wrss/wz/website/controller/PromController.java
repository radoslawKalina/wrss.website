package wrss.wz.website.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wrss.wz.website.model.request.PromSignUpRequest;
import wrss.wz.website.model.response.PromSignUpResponse;
import wrss.wz.website.service.interfaces.PromService;

import javax.validation.Valid;

@RestController
@RequestMapping("api/student/prom")
@RequiredArgsConstructor
public class PromController {

    public final PromService promService;

    @PostMapping
    public PromSignUpResponse signUp(@Valid @RequestBody PromSignUpRequest promSignUpRequest, Authentication authentication) {

        String username = authentication.getPrincipal().toString();
        return promService.signUp(promSignUpRequest, username);
    }
}