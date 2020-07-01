package io.incubator.wazx.oauth.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Noa Swartz
 */
@Service
public class CustomUserDetailsService {

    public UserDetails loadUserByUsername(String username, String userType) throws UsernameNotFoundException {
        return new User("admin", "123456", Collections.EMPTY_SET);
    }

}
