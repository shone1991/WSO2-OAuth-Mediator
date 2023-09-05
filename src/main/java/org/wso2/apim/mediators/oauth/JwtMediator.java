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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.apim.mediators.oauth.conf.ConfigReader;
import org.wso2.apim.mediators.oauth.conf.JWTEndpoint;
import org.wso2.apim.mediators.oauth.conf.OAuthEndpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Jwt mediator for generating OAuth tokens for invoking service endpoints
 * secured with OAuth2.
 */
public class JwtMediator extends AbstractMediator {

    private String endpointId;
    private static final Log log = LogFactory.getLog(JwtMediator.class);

    static {
        log.info("Starting to initialize JWT mediator");
        String carbonHome = System.getProperty("carbon.home");
        String confFilePath = carbonHome + File.separator + "repository" + File.separator + "conf" + File.separator
                + "wso2-jwt-mediator.json";
        try {
            List<JWTEndpoint> endpoints = ConfigReader.readJWTConfiguration(confFilePath);
            TokenGeneratorScheduledExecutor scheduledExecutor = new TokenGeneratorScheduledExecutor();
            for (JWTEndpoint endpoint : endpoints) {
                scheduledExecutor.schedule(endpoint);
            }
        } catch (FileNotFoundException e) {
            log.error("Configuration file not found: " + confFilePath);
            throw new RuntimeException(e);
        }
        log.info("OAuth mediator initialized successfully");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean mediate(MessageContext messageContext) {
        if (log.isDebugEnabled()) {
            log.debug("OAuth mediator :: mediate method invoked");
        }
        String accessToken = TokenCache.getInstance().getTokenMap().get(getEndpointId());
        Map<String, Object> transportHeaders = (Map<String, Object>) ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
        transportHeaders.put("Authorization", "Bearer " + accessToken);
        if (log.isDebugEnabled()) {
            log.debug("Retrieved access token from the Token endpoint :: " + getEndpointId() + " access_token = "
                    + accessToken);
        }
        return true;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
