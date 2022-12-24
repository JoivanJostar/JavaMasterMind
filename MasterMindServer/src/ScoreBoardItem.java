import java.io.Serializable;

public class ScoreBoardItem implements Serializable {
    static final long serialVersionUID= 3709832000L;
    private int PlayerId;
    private String PlayerName;
    private String status;//"Finish" "Escape" "Lost"
    private int turns;
    private long timeCost;

    public ScoreBoardItem(int playerId, String playerName, String status, int turns, long timeCost) {
        PlayerId = playerId;
        PlayerName = playerName;
        this.status = status;
        this.turns = turns;
        this.timeCost = timeCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(long timeCost) {
        this.timeCost = timeCost;
    }
}
