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

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.register(request);
        });

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

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.login(badLoginRequest);
        });

        Assertions.assertTrue(exception.getMessage().contains("401"));
    }
/*
    @Test
    @DisplayName("Test Name")
    void testSuccess() {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@test.com");

    }

    @Test
    @DisplayName("Test Name")
    void testFail() throws Exception {

    }*/
}
