package dataaccess;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collection;
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

    @Test
    @DisplayName("Create Game - Negative (Null Game Name)")
    public void createGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(null);
        }, "Creating a game with a null name should throw a DataAccessException");
    }

    @Test
    @DisplayName("Get Game - Negative (Non-existent ID)")
    public void getGameNegative() throws DataAccessException {
        GameData gameData = gameDAO.getGame(99999);
        assertNull(gameData, "Retrieving a non-existent game ID should return null");
    }

    @Test
    @DisplayName("List Games - Positive")
    public void listGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game One");
        gameDAO.createGame("Game Two");

        Collection<GameData> games = gameDAO.listGames();
        assertNotNull(games);
        assertEquals(2, games.size(), "Should list all created games");
    }

    @Test
    @DisplayName("List Games - Negative (Empty Database)")
    public void listGamesNegative() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();

        assertNotNull(games, "The game list should not be null, even if empty");
        assertTrue(games.isEmpty(), "Listing games on an empty database should return an empty collection");
    }

    @Test
    @DisplayName("Update Game - Positive (Join Game)")
    public void updateGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Joinable Game");
        GameData original = gameDAO.getGame(gameID);

        gameDAO.updateGame(original.gameID(), "whitePlayer", "WHITE");

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("whitePlayer", retrieved.whiteUsername());
    }

    @Test
    @DisplayName("Update Game - Negative (Non-existent Game ID)")
    public void updateGameNegative() throws DataAccessException {
        int fakeGameID = 999999;

        assertDoesNotThrow(() -> {
            gameDAO.updateGame(fakeGameID, "fakeUser", "WHITE");
        }, "Updating a non-existent game should affect 0 rows");

        GameData retrieved = gameDAO.getGame(fakeGameID);
        assertNull(retrieved, "The fake game does not exist in the database");
    }

    @Test
    @DisplayName("Clear Game Data")
    public void clearGameData() throws DataAccessException {
        gameDAO.createGame("Temp Game");
        gameDAO.clearAllGameData();

        assertTrue(gameDAO.listGames().isEmpty(), "Game list be empty after clearing");
    }
}