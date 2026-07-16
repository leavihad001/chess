package service;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    private UserService userService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    // --- REGISTERING ---

    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResult result = userService.register(request);

        Assertions.assertNotNull(result.authToken(), "AuthToken should not be null upon successful registration");
        Assertions.assertEquals("testUser", result.username(), "Username should match the registered user");
    }

    @Test
    public void registerFailDuplicateUser() throws Exception {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@email.com");
        userService.register(request);

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            userService.register(request);
        }, "Registering a duplicate user should throw an AlreadyTakenException");
    }

    // --- LOGIN ---

    @Test
    public void loginSuccess() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        userService.register(regRequest);

        LoginRequest loginRequest = new LoginRequest("testUser", "testPass");
        LoginResult result = userService.login(loginRequest);
        Assertions.assertNotNull(result.authToken(), "AuthToken should be returned on successful login");
    }

    @Test
    public void loginFailWrongPassword() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        userService.register(regRequest);
        LoginRequest loginRequest = new LoginRequest("testUser", "WRONG_PASSWORD");

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        }, "Logging in with the wrong password should throw an UnauthorizedException");
    }

    // --- LOGOUT ---

    @Test
    public void logoutSuccess() throws Exception {
        RegisterRequest regRequest = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResult regResult = userService.register(regRequest);

        Assertions.assertDoesNotThrow(() -> {
            userService.logout(regResult.authToken());
        });
    }

    @Test
    public void logoutFailBadToken() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            userService.logout("fake-or-expired-token");
        }, "Logging out with an invalid token should throw an UnauthorizedException");
    }
}