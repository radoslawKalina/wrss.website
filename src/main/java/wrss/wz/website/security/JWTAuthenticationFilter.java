package wrss.wz.website.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import wrss.wz.website.entity.StudentEntity;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static wrss.wz.website.security.SecurityConstants.EXPIRATION_TIME;
import static wrss.wz.website.security.SecurityConstants.HEADER;
import static wrss.wz.website.security.SecurityConstants.SECRET;
import static wrss.wz.website.security.SecurityConstants.TOKEN_PREFIX;

@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {

        try {
            StudentEntity user = new ObjectMapper().readValue(req.getInputStream(), StudentEntity.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
        UserDetailsImpl user = ((UserDetailsImpl) auth.getPrincipal());

        List<String> authorities = user.getAuthorities()
                                       .stream()
                                       .map(GrantedAuthority::getAuthority)
                                       .collect(Collectors.toList());

        String token = Jwts.builder()
                           .setSubject(user.getPassword())
                           .claim("authorities", authorities)
                           .claim("username", user.getUsername())
                           .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                           .signWith(SignatureAlgorithm.HS512, SECRET)
                           .compact();

        res.addHeader(HEADER, TOKEN_PREFIX + token);
    }
}

