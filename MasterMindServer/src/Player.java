import java.io.Serializable;
import java.net.Socket;

public class Player implements Serializable {
    static int ID=0;
    static final long serialVersionUID= 22031212417L;
    private int id;
    private String playerName;
    private String ipAddr;
    private int port;
    transient private Socket conn;
    public Player(String playerName, String ipAddr, int port) {
        this.id=Player.ID++;
        this.playerName = playerName;
        this.ipAddr = ipAddr;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public int getPort() {
        return port;
    }

    public Socket getConn() {
        return conn;
    }

    public void setConn(Socket conn) {
        this.conn = conn;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", ipAddr='" + ipAddr + '\'' +
                ", port=" + port +
                '}';
    }
}
