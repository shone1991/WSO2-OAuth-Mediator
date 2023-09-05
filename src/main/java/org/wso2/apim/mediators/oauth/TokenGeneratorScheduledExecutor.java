/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.apim.mediators.oauth;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.apim.mediators.oauth.client.JWTOauthClient;
import org.wso2.apim.mediators.oauth.client.OAuthClient;
import org.wso2.apim.mediators.oauth.client.domain.TokenResponse;
import org.wso2.apim.mediators.oauth.conf.JWTEndpoint;
import org.wso2.apim.mediators.oauth.conf.OAuthEndpoint;

/**
 * OAuth token generator scheduled executor.
 */
public class TokenGeneratorScheduledExecutor {

    private ScheduledExecutorService executorService;
    private static final Log log = LogFactory.getLog(TokenGeneratorScheduledExecutor.class);

    public TokenGeneratorScheduledExecutor() {
        this.executorService = new ScheduledThreadPoolExecutor(5);
    }

    /**
     * Initialize oauth client scheduled executor
     */
    public void schedule(OAuthEndpoint oAuthEndpoint) {
        try {
            log.info("Scheduling token generator for oauth2 token endpoint " + getEndpointId(oAuthEndpoint));

            executorService.scheduleAtFixedRate(() -> {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Generating access token: " + getEndpointId(oAuthEndpoint));
                    }

                    TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                            oAuthEndpoint.getApiKey(), oAuthEndpoint.getApiSecret(), oAuthEndpoint.getUsername(),
                            oAuthEndpoint.getPassword(), oAuthEndpoint.getGrantType(), oAuthEndpoint.getScope());

                    if (log.isDebugEnabled()) {
                        log.debug("Access token generated: " + getEndpointId(oAuthEndpoint) + " [access-token] "
                                + tokenResponse.getAccessToken());
                    }
                    TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse.getAccessToken());
                } catch (Exception e) {
                    log.error("Could not generated access token " + getEndpointId(oAuthEndpoint), e);
                }
            }, 0, oAuthEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public void schedule(JWTEndpoint jwtEndpoint) {
        try {
            log.info("Scheduling token generator for jwt token endpoint " + getEndpointId(jwtEndpoint));

            executorService.scheduleAtFixedRate(() -> {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Generating access token: " + getEndpointId(jwtEndpoint));
                    }

                    TokenResponse tokenResponse = JWTOauthClient.generateToken(jwtEndpoint.getTokenApiUrl(),
                            jwtEndpoint.getApiKey(), jwtEndpoint.getApiSecret(), jwtEndpoint.getUsername(),
                            jwtEndpoint.getPassword(), jwtEndpoint.getGrantType(), jwtEndpoint.getScope());

                    if (log.isDebugEnabled()) {
                        log.debug("Access token generated: " + getEndpointId(jwtEndpoint) + " [access-token] "
                                + tokenResponse.getAccessToken());
                    }
                    TokenCache.getInstance().getTokenMap().put(jwtEndpoint.getId(), tokenResponse.getAccessToken());
                } catch (Exception e) {
                    log.error("Could not generated access token " + getEndpointId(jwtEndpoint), e);
                }
            }, 0, jwtEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[id] " + oAuthEndpoint.getId() + " [url] " + oAuthEndpoint.getTokenApiUrl();
    }

    private String getEndpointId(JWTEndpoint jwtEndpoint) {
        return "[id] " + jwtEndpoint.getId() + " [url] " + jwtEndpoint.getTokenApiUrl();
    }
}
