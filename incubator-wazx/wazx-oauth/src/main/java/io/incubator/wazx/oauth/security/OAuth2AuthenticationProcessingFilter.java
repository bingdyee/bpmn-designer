package io.incubator.wazx.oauth.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Noa Swartz
 */
public class OAuth2AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private static final String PROCESSES_URL = "/oauth/token";
    private boolean postOnly = true;

    protected OAuth2AuthenticationProcessingFilter() {
        super(PROCESSES_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
//
//        String mobile = request.getParameter("username");
//
//        if (mobile == null) {
//            mobile = "";
//        }
//
//        mobile = mobile.trim();
//
//        UsernamePasswordAuthenticationToken mobileAuthenticationToken = new UsernamePasswordAuthenticationToken(mobile);
//
//        setDetails(request, mobileAuthenticationToken);
//
//        return this.getAuthenticationManager().authenticate(mobileAuthenticationToken);
        return null;
    }

    protected void setDetails(HttpServletRequest request,
                              UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }



    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public boolean isPostOnly() {
        return postOnly;
    }

}
