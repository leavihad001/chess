package service;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    private ClearService clearService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void clearSuccess() throws Exception {
        userDAO.createUser(new UserData("testUser", "password", "email@test.com"));
        authDAO.createAuth(new AuthData("testAuthToken", "testUser"));
        gameDAO.createGame("testGame");

        Assertions.assertNotNull(userDAO.getUser("testUser"));
        Assertions.assertNotNull(authDAO.getAuth("testAuthToken"));
        Assertions.assertFalse(gameDAO.listGames().isEmpty());

        clearService.clear();

        Assertions.assertNull(userDAO.getUser("testUser"), "UserDAO should be empty after clear");
        Assertions.assertNull(authDAO.getAuth("testAuthToken"), "AuthDAO should be empty after clear");
        Assertions.assertTrue(gameDAO.listGames().isEmpty(), "GameDAO should be empty after clear");
    }
}