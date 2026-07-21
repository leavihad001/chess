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

}