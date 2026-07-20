package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLGameDAO implements GameDAO {

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        // Notice gameID is AUTO_INCREMENT, and game_json is TEXT to hold the large JSON string
        var createTableStatement = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game_json TEXT NOT NULL,
                PRIMARY KEY (gameID)
            )
            """;

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createTableStatement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure game database. " + ex.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game_json) VALUES (?, ?, ?, ?)";

        // a fresh ChessGame board
        var newGame = new ChessGame();
        var jsonGame = new Gson().toJson(newGame);

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, null);
            preparedStatement.setString(2, null);
            preparedStatement.setString(3, gameName);
            preparedStatement.setString(4, jsonGame);

            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("Error: Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to create game. " + ex.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game_json FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setInt(1, gameID);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return readGameData(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to read game. " + ex.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game_json FROM games";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                result.add(readGameData(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to list games. " + ex.getMessage());
        }
        return result;
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        String statement;
        if (playerColor != null && playerColor.equalsIgnoreCase("WHITE")) {
            statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
        } else if (playerColor != null && playerColor.equalsIgnoreCase("BLACK")) {
            statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
        } else {
            //Observer option
            return;
        }

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to update game. " + ex.getMessage());
        }
    }

    @Override
    public void clearAllGameData() throws DataAccessException {
        var statement = "TRUNCATE TABLE games";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to clear game data. " + ex.getMessage());
        }
    }

    // Helper method to deserialize the JSON back into a GameData object
    private GameData readGameData(ResultSet resultSet) throws SQLException {
        int gameID = resultSet.getInt("gameID");
        String whiteUsername = resultSet.getString("whiteUsername");
        String blackUsername = resultSet.getString("blackUsername");
        String gameName = resultSet.getString("gameName");
        String gameJson = resultSet.getString("game_json");

        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}