package dataaccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyGameDAOTest {

    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySQLGameDAO();
        gameDAO.clearAllGameData();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.clearAllGameData();
    }

    @Test
    @DisplayName("Create and Get Game - Positive")
    public void createAndGetGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("My Test Game");
        assertTrue(gameID > 0, "Game ID should be a positive integer");

        GameData gameData = gameDAO.getGame(gameID);
        assertNotNull(gameData);
        assertEquals(gameID, gameData.gameID());
        assertEquals("My Test Game", gameData.gameName());
        assertNotNull(gameData.game());
    }
}