package dataaccess;
import model.UserData;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class MySQLUserDAO implements UserDAO {

    public MySQLUserDAO() throws DataAccessException{
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        var createTableStatement = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """;

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(createTableStatement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to configure database. " + ex.getMessage());
        }


    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        String hashed = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection(); var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, hashed);
            preparedStatement.setString(3, userData.email());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to create user. "  + ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    String retrievedUsername = resultSet.getString("username");
                    String retrievedPasswordHashed = resultSet.getString("password");
                    String retrievedEmail = resultSet.getString("email");

                    return new UserData(retrievedUsername, retrievedPasswordHashed, retrievedEmail);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to read user. " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            throw new DataAccessException("Error: Unable to clear user data. " + ex.getMessage());
        }
    }
}