package wrss.wz.website.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static wrss.wz.website.security.SecurityConstants.HEADER;
import static wrss.wz.website.security.SecurityConstants.SECRET;
import static wrss.wz.website.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        try {
            if (checkJWTToken(request)) {
                Claims claims = validateToken(request);

                if (claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);

                } else {
                    SecurityContextHolder.clearContext();
                }

            } else {
                SecurityContextHolder.clearContext();
            }

            chain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private Claims validateToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER);
        return Jwts.parser()
                   .setSigningKey(SECRET)
                   .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                   .getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        List<String> authorities = (List<String>) claims.get("authorities");
        List<SimpleGrantedAuthority> roles = authorities.stream()
                                                        .map(SimpleGrantedAuthority::new)
                                                        .collect(toList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.get("username"), null, roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean checkJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(TOKEN_PREFIX);
    }
}
