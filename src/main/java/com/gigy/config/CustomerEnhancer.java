package com.gigy.config;

import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * @author vincent
 * @date 08/12/2017
 */
public class CustomerEnhancer implements TokenEnhancer {

    private ClientDetailsService clientDetailsService;

    public void setClientDetailsService(ClientDetailsService clientDetailsService){
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String clientId = authentication.getOAuth2Request().getClientId();
        Map<String, Object> additionalInfo  = clientDetailsService.loadClientByClientId(clientId)
            .getAdditionalInformation();
        if(additionalInfo.get("region") != null){
            String value = accessToken.getValue() + "-" + additionalInfo.get("region");
            ((DefaultOAuth2AccessToken)accessToken).setValue(value);
        }else{
            //TODO: 没有region信息，应该返回4XX的错误，表示授权错误
            throw new InvalidTokenException("invalid region info");
        }
        return accessToken;
    }
}
