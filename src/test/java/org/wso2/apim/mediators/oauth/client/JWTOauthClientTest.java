package org.wso2.apim.mediators.oauth.client;

import org.junit.Before;
import org.junit.Test;
import org.wso2.apim.mediators.oauth.client.domain.TokenResponse;
import org.wso2.apim.mediators.oauth.conf.ConfigReader;
import org.wso2.apim.mediators.oauth.conf.JWTEndpoint;
import org.wso2.apim.mediators.oauth.conf.OAuthEndpoint;
import org.wso2.apim.mediators.oauth.util.ParameterStringBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project WSO2-OAuth-Mediator
 * on 9/5/2023
 */
public class JWTOauthClientTest {
    List<JWTEndpoint> actual;

    @Before
    public void init(){
        Path resourceDirectory = Paths.get("src", "test", "resources", "wso2-jwt-mediator.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        try {
            actual = ConfigReader.readJWTConfiguration(absolutePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateToken() throws IOException {
        actual.forEach(e-> System.out.println(e.getTokenApiUrl()));
        JWTEndpoint endpoint=actual.get(0);
        final TokenResponse tokenResponse = JWTOauthClient.generateToken(endpoint.getTokenApiUrl(), endpoint.getApiKey(), endpoint.getApiSecret(),
                endpoint.getUsername(), endpoint.getPassword(), endpoint.getGrantType(), endpoint.getScope());
        assert tokenResponse != null;
        System.out.println(tokenResponse.getAccessToken());
    }

    @Test
    public void temp() throws IOException {
        HttpURLConnection connection = null;
        String urlEncodedParams = null;

        URL url_ = new URL("https://localhost:9445/oauth2/token");
        connection = (HttpURLConnection) url_.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        StringBuffer response = new StringBuffer();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "client_credentials");
        parameters.put("scope", "default");

        String credentials = Base64.getEncoder().encodeToString(("6QYpURlOeaPMC7xYPnB1fGmXwLga" + ":" + "cuzGyfxtWNAC06DxYRwiaD67SNEa").getBytes());
        connection.setRequestProperty("Authorization", "Basic " + credentials);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        connection.setDoOutput(true);
        final String paramsString = ParameterStringBuilder.getParamsString(parameters);
        connection.setRequestProperty("Content-Length", Integer.toString(paramsString.getBytes().length));
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(paramsString);
        out.flush();
        out.close();
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        System.out.println(response+" "+responseCode);
    }


}