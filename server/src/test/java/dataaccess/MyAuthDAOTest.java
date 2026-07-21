package dataaccess;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MyAuthDAOTest {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MySQLAuthDAO();
        authDAO.clearAllAuthData();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        authDAO.clearAllAuthData();
    }

    @Test
    @DisplayName("Create Auth - Positive")
    public void createAuthPositive() throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(token, "testUser");

        authDAO.createAuth(newAuth);

        AuthData retrieved = authDAO.getAuth(token);
        assertNotNull(retrieved, "AuthData should be found in the database");
        assertEquals(token, retrieved.authToken());
        assertEquals("testUser", retrieved.username());
    }

    @Test
    @DisplayName("Create Auth - Negative (Duplicate Token)")
    public void createAuthNegative() throws DataAccessException {
        AuthData newAuth = new AuthData("duplicate-token", "testUser");
        authDAO.createAuth(newAuth);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(newAuth);
        }, "Inserting a duplicate auth token should throw a DataAccessException");
    }

    @Test
    @DisplayName("Get Auth - Positive")
    public void getAuthPositive() throws DataAccessException {
        AuthData newAuth = new AuthData("my-custom-token-123", "testUser");
        authDAO.createAuth(newAuth);

        AuthData retrieved = authDAO.getAuth("my-custom-token-123");

        assertNotNull(retrieved);
        assertEquals(newAuth.authToken(), retrieved.authToken());
        assertEquals("testUser", retrieved.username());
    }

    @Test
    @DisplayName("Get Auth - Negative (Invalid Token)")
    public void getAuthNegative() throws DataAccessException {
        AuthData retrieved = authDAO.getAuth("non-existent-token");
        assertNull(retrieved, "Retrieving a non-existent auth token should return null");
    }

    @Test
    @DisplayName("Delete Auth - Positive")
    public void deleteAuthPositive() throws DataAccessException {
        String token = "token-to-delete";
        AuthData newAuth = new AuthData(token, "testUser");
        authDAO.createAuth(newAuth);

        authDAO.deleteAuth(token);

        assertNull(authDAO.getAuth(token), "Auth token should be deleted and return null");
    }

    @Test
    @DisplayName("Delete Auth - Negative (Non-existent Token)")
    public void deleteAuthNegative() {
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("fake-token-123");
        }, "Deleting a non-existent token throws a DataAccessException");
    }

    @Test
    @DisplayName("Clear Auth Data")
    public void clearAuthData() throws DataAccessException {
        AuthData newAuth = new AuthData("clear-me-token", "clearUser");
        authDAO.createAuth(newAuth);

        authDAO.clearAllAuthData();

        assertNull(authDAO.getAuth("clear-me-token"), "Auth data table should be cleared");
    }
}