import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameRoom  implements Serializable {
    static final long serialVersionUID= 18050500130L;
    public enum RoomStatus{
        GAMING("Gaming"),FREE("Free");
        private String description;
        private RoomStatus(String description){
            this.description=description;
        }
    }
    private Map<Integer,Player> players;
    private String roomName;
    private int roomId;
    private int maxPlayerAmount;
    private RoomStatus status;
    transient private List<ScoreBoardItem> scoreBoard;
    public GameRoom(String roomName, int id, int maxPlayerAmount) {
        this.roomName = roomName;
        this.roomId = id;
        this.maxPlayerAmount = maxPlayerAmount;
        this.status=RoomStatus.FREE;
        this.players=new HashMap<Integer, Player>();
        this.scoreBoard=new LinkedList<ScoreBoardItem>();
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getMaxPlayerAmount() {
        return maxPlayerAmount;
    }
    public int getPresentPlayerNums() {
        return players.size();
    }
    public List<ScoreBoardItem> getScoreBoard() {
        return scoreBoard;
    }
    public void addPlayer(Player player){
        players.put(player.getId(),player);
    }
    public void removePlayer(Player player){
        players.remove(player.getId());
    }

}
