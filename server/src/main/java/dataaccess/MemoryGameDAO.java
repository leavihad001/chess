package dataaccess;
import chess.ChessGame;
import model.GameData;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData>games = new HashMap<>();

    private int nextGameID = 1;

    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    public int createGame(String gameName) throws DataAccessException{
        GameData nextGame = new GameData(nextGameID, null, null, gameName, new ChessGame());
        games.put(nextGameID, nextGame);
        return nextGameID++;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException{
        GameData currentGame = games.get(gameID);

        GameData updated;

        if (playerColor.equals("WHITE")) {
            updated = new GameData(gameID, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
        } else {
            updated = new GameData(gameID, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
        }

        games.put(gameID, updated);
    }

    public void clearAllGameData() throws DataAccessException {
        games.clear();
    }
}