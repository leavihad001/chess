package client;
import org.junit.jupiter.api.*;
import server.Server;
import requAndResu.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    @DisplayName("Register Success")
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@test.com");

        Assertions.assertDoesNotThrow(() -> {
            RegisterResult result = facade.register(request);

            Assertions.assertNotNull(result.authToken());
            Assertions.assertEquals("testUser", result.username());
        });
    }

    @Test
    @DisplayName("Register Fail - Duplicate User")
    void registerFail() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@test.com");

        facade.register(request);

        Exception exception = Assertions.assertThrows(Exception.class, () -> facade.register(request));

        Assertions.assertTrue(exception.getMessage().contains("403"));
    }

    @Test
    @DisplayName("Login Success")
    void loginSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");

        Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult result = facade.login(loginRequest);

            Assertions.assertEquals("testUser", result.username());
            Assertions.assertNotNull(result.authToken());
        });

    }

    @Test
    @DisplayName("Login Fail - Wrong Password")
    void loginFail() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest badLoginRequest = new LoginRequest("testUser", "wrongPass");

        Assertions.assertDoesNotThrow(() -> facade.register(registerRequest));

        Exception exception = Assertions.assertThrows(Exception.class, () -> facade.login(badLoginRequest));

        Assertions.assertTrue(exception.getMessage().contains("401"));
    }

    @Test
    @DisplayName("Logout Success")
    void logoutSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");

        Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult result = facade.login(loginRequest);
            facade.logout(result.authToken());
        });
    }

    @Test
    @DisplayName("Logout Fail - Bad AuthToken")
    void logoutFail() {

        Exception exception = Assertions.assertThrows(Exception.class, () -> facade.logout("bad-token"));

        Assertions.assertTrue(exception.getMessage().contains("401"));
    }

    @Test
    @DisplayName("Create Game Success")
    void createGameSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult result = facade.login(loginRequest);
            facade.CreateGame(createGameRequest, result.authToken());
        });
    }

    @Test
    @DisplayName("Create Game Fail - Bad token")
    void createGameFail() {
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        Exception exception = Assertions.assertThrows(Exception.class, () -> facade.CreateGame(createGameRequest, "bad-token"));

        Assertions.assertTrue(exception.getMessage().contains("401"));
    }

    @Test
    @DisplayName("List Games Success")
    void listGamesSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        String validToken = Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult result = facade.login(loginRequest);
            facade.CreateGame(createGameRequest, result.authToken());

            return result.authToken();
        });

        Assertions.assertDoesNotThrow(() -> {
            facade.listGames(validToken);
        });
    }

    @Test
    @DisplayName("List Games Fail - Bad Token")
    void listGamesFail() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult result = facade.login(loginRequest);
            facade.CreateGame(createGameRequest, result.authToken());
        });

        Exception exception = Assertions.assertThrows(Exception.class, () -> facade.listGames("bad-token"));

        Assertions.assertTrue(exception.getMessage().contains("401"));
    }

    @Test
    @DisplayName("Join Game Success")
    void JoinGameSuccess() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            return facade.login(loginRequest);
        });

        JoinGameRequest joinGameRequest = Assertions.assertDoesNotThrow(() -> {
            CreateGameResult createResult = facade.CreateGame(createGameRequest, loginResult.authToken());
            return new JoinGameRequest("WHITE", createResult.gameID());
        });

        Assertions.assertDoesNotThrow(() -> facade.joinGame(joinGameRequest, loginResult.authToken()));
    }

    @Test
    @DisplayName("Join Game Fail - Bad Game ID")
    void joinGameFail() {
        RegisterRequest registerRequest = new RegisterRequest("testUser", "testPass", "test@test.com");
        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        String validToken = Assertions.assertDoesNotThrow(() -> {
            facade.register(registerRequest);
            LoginResult loginResult = facade.login(loginRequest);
            facade.CreateGame(createGameRequest, loginResult.authToken());

            return loginResult.authToken();
        });
        
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 9999);
            facade.joinGame(joinGameRequest, validToken);
        });

        Assertions.assertTrue(exception.getMessage().contains("400"));
    }
}
