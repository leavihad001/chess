package ui;
import client.ServerFacade;
import model.GameData;
import reqsandres.*;
import java.util.*;

public class DashboardUI {
    private final ServerFacade facade;
    private final String serverURL;
    private final String authToken;
    private GameData[] gamesArray;

    public DashboardUI(String serverURL, String authToken) {
        this.facade = new ServerFacade(serverURL);
        this.authToken = authToken;
        this.serverURL = serverURL;
    }

    public void repl() {
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!Objects.equals(result, "logout")) {
            System.out.print("[LOGGED_IN] >>> ");
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                if (result != null && !Objects.equals(result, "logout")) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                System.out.print(e.getMessage() + "\n");
            }
        }
    }

    private String evaluate(String in) {
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

            ListGamesResult games = facade.listGames(authToken);
            this.gamesArray = games.games().toArray(new GameData[0]);

            var array = new StringBuilder();

            buildArray(array);

            return String.format("Game '%s' created. ID: %d\n", name, result.gameID());
        }
        throw new Exception("Expected: create <GAME-NAME>");
    }

    private String list() throws Exception {
        ListGamesResult games = facade.listGames(authToken);

        this.gamesArray = games.games().toArray(new GameData[0]);

        var array = new StringBuilder();

        array.append("Current Games: \n");

        buildArray(array);
        return array.toString();
    }

    private void buildArray(StringBuilder array) {
        for (int i = 0; i < gamesArray.length; i++) {
            GameData game = gamesArray[i];
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "EMPTY";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "EMPTY";
            array.append(String.format(" %d. %s\n", i + 1, game.gameName()));
            array.append(String.format("White: %s | Black: %s\n\n", whiteUser, blackUser));
        }
    }

    private String join(String[] params) throws Exception {
        if (params.length == 2) {
            if (gamesArray == null) {
                ListGamesResult games = facade.listGames(authToken);
                this.gamesArray = games.games().toArray(new GameData[0]);

                var array = new StringBuilder();

                buildArray(array);
                if (gamesArray == null) {
                    return "No games created. Please start a game to play.\n";
                }
            }
            try {
                int listNumber = Integer.parseInt(params[0]);
                if (listNumber < 1 || listNumber > gamesArray.length) {
                    return "Invalid game number. Please choose a number from the list.";
                }

                int realID = gamesArray[listNumber-1].gameID();
                String teamChoice = params[1].toUpperCase();

                JoinGameRequest request = new JoinGameRequest(teamChoice, realID);
                facade.joinGame(request, authToken);

                System.out.println("Successfully joined game \"" + gamesArray[listNumber-1].gameName() + "\" as " + teamChoice + ".");

                new GameUI(serverURL, authToken, realID, teamChoice).repl();

                return null;

            } catch (NumberFormatException e) {
                return "Expected a number for the game ID \n";
            }
        }
        throw new Exception("Expected: join <Game-Number> [WHITE or BLACK]");
    }

    private String observe(String[] params) throws Exception {
        if (params.length == 1) {
            if (gamesArray == null) {
                ListGamesResult games = facade.listGames(authToken);
                this.gamesArray = games.games().toArray(new GameData[0]);

                var array = new StringBuilder();

                buildArray(array);
                if (gamesArray == null) {
                    return "No games created.\n";
                }
            }
            try {
                int listNumber = Integer.parseInt(params[0]);
                if (listNumber < 1 || listNumber > gamesArray.length) {
                    return "Invalid game number. Please choose a number from the list.";
                }

                int realID = gamesArray[listNumber-1].gameID();

                JoinGameRequest request = new JoinGameRequest("OBSERVE", realID);
                facade.joinGame(request, authToken);

                System.out.println("Successfully joined game \"" + gamesArray[listNumber-1].gameName() + "\" as observer.");

                new GameUI(serverURL, authToken, realID, "OBSERVE").repl();

                return null;

            } catch (NumberFormatException e) {
                return "Expected a number for the game ID \n";
            }
        }
        throw new Exception("Expected: observe <Game-Number>");
    }

    private String logout() throws Exception {
        facade.logout(authToken);
        System.out.println("Successfully logged out.");
        return "logout";
    }
}