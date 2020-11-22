package wrss.wz.website.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wrss.wz.website.entity.StudentEntity;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final StudentEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return userEntity.getRoles()
                         .stream()
                         .map(role -> new SimpleGrantedAuthority(role.getRole()))
                         .collect(toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}