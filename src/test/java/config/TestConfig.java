package config;

public class TestConfig {

    //private static final String DEFAULT_BASE_URL = "http://localhost:5000";
    private static final String DEFAULT_BASE_URL = "https://e83d370e09cc.ngrok-free.app";

    public static String getBaseUrl() {
        String envUrl = System.getenv("DASHBOARD_BASE_URL");

        if (envUrl == null || envUrl.isBlank()) {
            return DEFAULT_BASE_URL;
        }

        return envUrl;
    }

    private TestConfig() {
        // prevent instantiation
    }
}
