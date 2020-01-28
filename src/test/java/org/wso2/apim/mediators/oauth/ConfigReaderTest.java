package org.wso2.apim.mediators.oauth;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.wso2.apim.mediators.oauth.conf.ConfigReader;
import org.wso2.apim.mediators.oauth.conf.OAuthEndpoint;

public class ConfigReaderTest {

    @Test
    public void testConfigReader() {
        Path resourceDirectory = Paths.get("src", "test", "resources", "wso2-oauth-mediator.json");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        List<OAuthEndpoint> expected = new ArrayList<>();
        OAuthEndpoint oauthEP = new OAuthEndpoint();
        oauthEP.setId("EP1");
        oauthEP.setTokenApiUrl("http://localhost:8280/token");
        oauthEP.setApiKey("1234");
        oauthEP.setApiSecret("4321");
        oauthEP.setUsername("admin");
        oauthEP.setPassword("admin");
        oauthEP.setScope("default");
        oauthEP.setTokenRefreshInterval(60);
        expected.add(oauthEP);

        try {
            List<OAuthEndpoint> actual = ConfigReader.readConfiguration(absolutePath);
            assertEquals(expected.get(0).getApiKey(), actual.get(0).getApiKey());
            assertEquals(expected.get(0).getApiSecret(), actual.get(0).getApiSecret());
            assertEquals(expected.get(0).getId(), actual.get(0).getId());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
