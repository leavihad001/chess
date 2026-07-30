package client;
import com.google.gson.Gson;
import reqsandres.*;
import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverURL;
    public ServerFacade(String url){
        this.serverURL = url;
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        return this.makeRequest("POST", "/user", request, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        return this.makeRequest("POST", "/session", request, LoginResult.class, null);
    }

    public void logout(String authToken) throws Exception {
        this.makeRequest("DELETE", "/session", null, null, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return this.makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws Exception {
        return this.makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws Exception {
        this.makeRequest("PUT", "/game", request, null, authToken);
    }

    public void clear() throws Exception {
        this.makeRequest("DELETE", "/db", null, null, null);
    }


    private <T> T makeRequest(String method, String path, Object request,
                              Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }

            if (request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String requestData = new Gson().toJson(request);
                try (OutputStream requestBody = http.getOutputStream()) {
                    requestBody.write(requestData.getBytes());
                }
            }

            http.connect();

            if (http.getResponseCode() >= 400) {
                handleError(http);
            }

            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Unable to connect to server.");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;

        if (responseClass != null) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                response = new Gson().fromJson(reader, responseClass);
            }
        }
        return response;
    }

    private void handleError(HttpURLConnection http) throws Exception {
        int status = http.getResponseCode();
        switch (status) {
            case 400 -> throw new Exception("Invalid request. Please check your input and try again.");
            case 401 -> throw new Exception("Unauthorized: Please log in again or check your credentials.");
            case 402 -> throw new Exception("User already taken.");
            case 403 -> throw new Exception("You do not have permission to join/modify this game.");
            case 404 -> throw new Exception("Not found: The requested game or resource does not exist.");
            case 500 -> throw new Exception("Server error: Something went wrong on the server.");
            default -> throw new Exception("An unexpected error occurred (Status code: " + status + ").");
        }
    }
}