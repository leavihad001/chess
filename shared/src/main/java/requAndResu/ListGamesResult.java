package requAndResu;
import model.GameData;
import java.util.Collection;

public record ListGamesResult(Collection<GameData> games) {}