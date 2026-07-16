package server;

import com.google.gson.Gson;
import service.*;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ListGamesResult result = gameService.listGames(authToken);

            ctx.status(200);
            return gson.toJson(result);

        } catch (UnauthorizedException e) {
            ctx.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    public Object createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

            CreateGameResult result = gameService.createGame(authToken, request);

            ctx.status(200);
            return gson.toJson(result);

        } catch (BadRequestException e) {
            ctx.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        } catch (UnauthorizedException e) {
            ctx.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    public Object joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

            gameService.joinGame(authToken, request);
            
            ctx.status(200);
            return "{}";

        } catch (BadRequestException e) {
            ctx.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        } catch (UnauthorizedException e) {
            ctx.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        } catch (DataAccessException e) {
            ctx.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}