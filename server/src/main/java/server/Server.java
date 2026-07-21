package server;
import dataaccess.*;
import io.javalin.Javalin;
import service.ClearService;
import service.UserService;
import service.GameService;

public class Server {

    public int run(int desiredPort) {
        Javalin javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDAO userDAO;
        AuthDAO authDAO;
        GameDAO gameDAO;

        try {
            userDAO = new MySQLUserDAO();
            authDAO = new MySQLAuthDAO();
            gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            //Still not quite sure why this fixes it
            System.err.println("Unable to initialize database: " + e.getMessage());
            throw new RuntimeException(e);
        }

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);

        ClearHandler clearHandler = new ClearHandler(clearService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler::logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);

        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        Javalin.create().stop();
    }
}