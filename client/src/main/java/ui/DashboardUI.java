package ui;
import client.ServerFacade;
import reqsandres.*;
import java.util.*;

public class DashboardUI {
    private final ServerFacade facade;
    private final String authToken;

    public DashboardUI(String serverURL, String authToken) {
        this.facade = new ServerFacade(serverURL);
        this.authToken = authToken;
    }

    public void repl() {
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!Objects.equals(result, "logout")) {
            System.out.print("[LOGGED_IN] >>> ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    private String eval(String in) {
        try {
            var tokens = in.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - help - with possible commands
                """;
    }

    private String create(String[] params) throws Exception {
        if (params.length == 1) {
            String name = params[0];

            CreateGameRequest request = new CreateGameRequest(name);
            CreateGameResult result = facade.createGame(request, authToken);

            return String.format("Game '%s' created. ID: %d\n", name, result.gameID());
        }
        throw new Exception("Expected: create <GAME-NAME>");
    }

    private String list() throws Exception {
        ListGamesResult result = facade.listGames(authToken);

        //need to list them out here

        return null;
    }

    private String join(String[] params) throws Exception {
        return null;
    }

    private String observe(String[] params) throws Exception {
        return null;
    }

    private String logout() throws Exception {
        facade.logout(authToken);
        System.out.println("Successfully logged out.");
        return "logout";
    }
}