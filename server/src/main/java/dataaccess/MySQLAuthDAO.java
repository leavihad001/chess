package dataaccess;

import model.AuthData;
import java.sql.SQLException;

public class MySQLAuthDAO implements AuthDAO {

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        var createTableStatement = """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )
            """;

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createTableStatement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure auth database. " + ex.getMessage());
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authData.authToken());
            preparedStatement.setString(2, authData.username());

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to create auth token. " + ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String retrievedToken = resultSet.getString("authToken");
                    String retrievedUsername = resultSet.getString("username");

                    return new AuthData(retrievedToken, retrievedUsername);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to read auth token. " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);

            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new DataAccessException("Error: Auth token not found to delete.");
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to delete auth token. " + ex.getMessage());
        }
    }

    @Override
    public void clearAllAuthData() throws DataAccessException {
        var statement = "TRUNCATE TABLE auth";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to clear auth data. " + ex.getMessage());
        }
    }
}