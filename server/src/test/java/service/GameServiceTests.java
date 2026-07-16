package service;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {

    private GameService gameService;
    private UserService userService;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;

    private String validAuthToken;

    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        RegisterResult regResult = userService.register(new RegisterRequest("testUser", "testPass", "test@email.com"));
        validAuthToken = regResult.authToken();
    }

    // --- CREATE GAME ---

    @Test
    public void createGameSuccess() throws Exception {
        CreateGameRequest request = new CreateGameRequest("My Awesome Game");
        CreateGameResult result = gameService.createGame(validAuthToken, request);

        Assertions.assertTrue(result.gameID() > 0, "A valid game ID should be returned");
    }

    @Test
    public void createGameFailUnauthorized() {
        CreateGameRequest request = new CreateGameRequest("My Awesome Game");

        Assertions.assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame("fake-token", request);
        }, "Creating a game with a bad token should throw an UnauthorizedException");
    }

    // --- LIST GAMES ---

    @Test
    public void listGamesSuccess() throws Exception {
        // Create a couple of games first
        gameService.createGame(validAuthToken, new CreateGameRequest("Game 1"));
        gameService.createGame(validAuthToken, new CreateGameRequest("Game 2"));

        ListGamesResult result = gameService.listGames(validAuthToken);
        Assertions.assertEquals(2, result.games().size(), "List games should return exactly 2 games");
    }

    @Test
    public void listGamesFailUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("invalid-token");
        }, "Listing games with an invalid token should throw an UnauthorizedException");
    }

    // --- JOIN GAME ---

    @Test
    public void joinGameSuccess() throws Exception {
        CreateGameResult createResult = gameService.createGame(validAuthToken, new CreateGameRequest("Join Me"));
        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", createResult.gameID());

        Assertions.assertDoesNotThrow(() -> {
            gameService.joinGame(validAuthToken, joinRequest);
        });
    }

    @Test
    public void joinGameFailAlreadyTaken() throws Exception {
        CreateGameResult createResult = gameService.createGame(validAuthToken, new CreateGameRequest("Join Me"));

        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", createResult.gameID());
        gameService.joinGame(validAuthToken, joinRequest);
        RegisterResult user2Result = userService.register(new RegisterRequest("user2", "pass", "email"));

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(user2Result.authToken(), joinRequest);
        }, "Trying to claim an already claimed spot should throw an AlreadyTakenException");
    }
}