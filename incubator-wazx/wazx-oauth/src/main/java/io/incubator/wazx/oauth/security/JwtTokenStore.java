package io.incubator.wazx.oauth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

/**
 * @author Noa Swartz
 */
public class JwtTokenStore implements TokenStore {

    private ApprovalStore approvalStore;

    /**
     * ApprovalStore to be used to validate and restrict refresh tokens.
     *
     * @param approvalStore the approvalStore to set
     */
    public void setApprovalStore(ApprovalStore approvalStore) {
        this.approvalStore = approvalStore;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return null;
    }



    @Override
    @Deprecated
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return null;
    }

    @Override
    @Deprecated
    public void removeAccessToken(OAuth2AccessToken token) {
    }

    @Override
    @Deprecated
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2AccessToken encodedRefreshToken = convertAccessToken(tokenValue);
        OAuth2RefreshToken refreshToken = createRefreshToken(encodedRefreshToken);
        if (approvalStore != null) {
            OAuth2Authentication authentication = readAuthentication(tokenValue);
            if (authentication.getUserAuthentication() != null) {
                String userId = authentication.getUserAuthentication().getName();
                String clientId = authentication.getOAuth2Request().getClientId();
                Collection<Approval> approvals = approvalStore.getApprovals(userId, clientId);
                Collection<String> approvedScopes = new HashSet<String>();
                for (Approval approval : approvals) {
                    if (approval.isApproved()) {
                        approvedScopes.add(approval.getScope());
                    }
                }
                if (!approvedScopes.containsAll(authentication.getOAuth2Request().getScope())) {
                    return null;
                }
            }
        }
        return refreshToken;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        remove(token.getValue());
    }

    @Override
    @Deprecated
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
    }

    @Override
    @Deprecated
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return Collections.emptySet();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return Collections.emptySet();
    }

    private void remove(String token) {
        if (approvalStore != null) {
            OAuth2Authentication auth = readAuthentication(token);
            String clientId = auth.getOAuth2Request().getClientId();
            Authentication user = auth.getUserAuthentication();
            if (user != null) {
                Collection<Approval> approvals = new ArrayList<Approval>();
                for (String scope : auth.getOAuth2Request().getScope()) {
                    approvals.add(new Approval(user.getName(), clientId, scope, new Date(), ApprovalStatus.APPROVED));
                }
                approvalStore.revokeApprovals(approvals);
            }
        }
    }

    private OAuth2AccessToken convertAccessToken(String tokenValue) {
//        return jwtTokenEnhancer.extractAccessToken(tokenValue, jwtTokenEnhancer.decode(tokenValue));
        return null;
    }

    private OAuth2RefreshToken createRefreshToken(OAuth2AccessToken encodedRefreshToken) {
//        if (!jwtTokenEnhancer.isRefreshToken(encodedRefreshToken)) {
//            throw new InvalidTokenException("Encoded token is not a refresh token");
//        }
//        if (encodedRefreshToken.getExpiration()!=null) {
//            return new DefaultExpiringOAuth2RefreshToken(encodedRefreshToken.getValue(),
//                    encodedRefreshToken.getExpiration());
//        }
        return new DefaultOAuth2RefreshToken(encodedRefreshToken.getValue());
    }

}
