package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyUserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MySQLUserDAO();
        userDAO.clearUserData();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDAO.clearUserData();
    }

    @Test
    @DisplayName("Insert User - Positive")
    public void insertUserPositive() throws DataAccessException {
        UserData user = new UserData("testUser", "securePassword123", "test@mail.com");
        userDAO.createUser(user);

        UserData retrieved = userDAO.getUser("testUser");
        assertNotNull(retrieved, "User was not found in the database");
        assertEquals("testUser", retrieved.username());
        assertNotEquals("securePassword123", retrieved.password());
    }

    @Test
    @DisplayName("Insert User - Negative (Duplicate Username)")
    public void insertUserNegative() throws DataAccessException {
        UserData user1 = new UserData("duplicateUser", "password123", "dup1@mail.com");
        userDAO.createUser(user1);

        UserData user2 = new UserData("duplicateUser", "differentPassword", "dup2@mail.com");
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user2);
        }, "Inserting a user with a duplicate username should throw DataAccessException");
    }

    @Test
    @DisplayName("Get User - Positive")
    public void getUserPositive() throws DataAccessException {
        UserData user = new UserData("findMe", "password", "find@mail.com");
        userDAO.createUser(user);

        UserData found = userDAO.getUser("findMe");
        assertNotNull(found);
        assertEquals("findMe", found.username());
    }

    @Test
    @DisplayName("Get User - Negative (Non-existent User)")
    public void getUserNegative() throws DataAccessException {
        UserData found = userDAO.getUser("ghostUser");
        assertNull(found, "Getting a non-existent user should return null");
    }

    @Test
    @DisplayName("Clear User Data")
    public void clearUserData() throws DataAccessException {
        UserData user = new UserData("clearUser", "password", "clear@mail.com");
        userDAO.createUser(user);

        userDAO.clearUserData();
        assertNull(userDAO.getUser("clearUser"), "User data should be cleared");
    }
}