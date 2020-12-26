package wrss.wz.website.interceptor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        request.setAttribute("username", username);

        return true;
    }
}
