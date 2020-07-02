package io.incubator.wazx.oauth.security;

import io.incubator.wazx.oauth.pojo.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author Noa Swartz
 */
@Service
public class CustomUserDetailsService {

    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(2L);
        principal.setUsername("user");
        principal.setStatus(1);
        principal.setPassword("$2a$10$O.tMi2VaYPX/jXBY856CGOZAra6rsEujTID55RUwtj/Hikh1Mmuc2");
        principal.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN")));
        return principal;
    }

    public UserPrincipal loadMemberByUsername(String username) throws UsernameNotFoundException {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(2L);
        principal.setStatus(1);
        principal.setUsername("member");
        principal.setPassword("$2a$10$O.tMi2VaYPX/jXBY856CGOZAra6rsEujTID55RUwtj/Hikh1Mmuc2");
        principal.setAuthorities(Arrays.asList(new SimpleGrantedAuthority("MEMBER"), new SimpleGrantedAuthority("USER")));
        return principal;
    }

}
