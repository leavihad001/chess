package ui;
import client.ServerFacade;
import reqsandres.LoginRequest;
import reqsandres.LoginResult;
import reqsandres.RegisterRequest;
import reqsandres.RegisterResult;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class PreloginUI {
    private final ServerFacade facade;

    public PreloginUI(String serverURL) {
        this.facade = new ServerFacade(serverURL);
    }

    public void repl() {
        System.out.println("♕ Welcome to CS 240 Chess! Type 'help' to get started. ♕");
        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!Objects.equals(result, "quit")) {
            System.out.print("[LOGGED_OUT] >>> ");
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
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    private String register(String[] params) throws Exception {
        if (params.length == 3) {
            String user = params[0];
            String pass = params[1];
            String email = params[2];

            RegisterRequest request = new RegisterRequest(user, pass, email);
            RegisterResult result = facade.register(request);

            //new DashboardUI(serverURL, result.authToken()).run();
            //I still need to implement this part ^^^

            return null;
        }

        throw new Exception("Expected: register <USERNAME> <PASSWORD> <EMAIL>");


    }

    private String login(String[] params) throws Exception {
        if (params.length == 2) {
            String user = params[0];
            String pass = params[1];

            LoginRequest request = new LoginRequest(user, pass);
            LoginResult result = facade.login(request);

            //new DashboardUI(serverURL, result.authToken()).run();
            //I still need to implement this part ^^^

            return null;
        }
        throw new Exception("Expected: login <USERNAME> <PASSWORD>");
    }
}