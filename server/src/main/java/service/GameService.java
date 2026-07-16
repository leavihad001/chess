package service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;


public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedException, DataAccessException {
        verifyAuth(authToken);
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws UnauthorizedException, BadRequestException, DataAccessException {
        verifyAuth(authToken);

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        int newGameId = gameDAO.createGame(request.gameName());
        return new CreateGameResult(newGameId);
    }

    public void joinGame(String authToken, JoinGameRequest request) throws UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        AuthData authData = verifyAuth(authToken);

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.playerColor() == null || (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
            throw new BadRequestException("Error: bad request");
        }

        if (request.playerColor().equals("WHITE") && game.whiteUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        } else if (request.playerColor().equals("BLACK") && game.blackUsername() != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        gameDAO.updateGame(request.gameID(), authData.username(), request.playerColor());
    }

    private AuthData verifyAuth(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authData;
    }
}