package io.incubator.wazx.oauth.security;

import io.incubator.wazx.oauth.pojo.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author Noa Swartz
 */
@Component
public class CustomAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        System.err.println(userDetails);
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        LoginParam param = (LoginParam) authentication.getDetails();
        return customUserDetailsService.loadUserByUsername(param.getUsername(), param.getUserType());
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        this.setHideUserNotFoundExceptions(false);
    }

}
