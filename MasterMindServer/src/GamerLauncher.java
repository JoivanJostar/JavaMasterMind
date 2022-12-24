import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GamerLauncher extends Thread {
    Map<Integer,Player> players;
    GameRoom room;
    ScoreBoard scoreBoard;
    public GamerLauncher(Map<Integer,Player> players, GameRoom room){
        this.players=players;
        this.room=room;
        this.scoreBoard=new ScoreBoard();
    }

    @Override
    public void run() {
        System.out.println("GamerLauncher "+this.getName()+" started");
        List<Thread> threads=new LinkedList<Thread>();
        //N个玩家生成同一份随机码
        String secretCode = SecretCodeGenerator.getInstance().getNewSecretCode();
        //创建游戏线程
        for (Player player : players.values()) {
            Thread t=new PlayGame(player,secretCode,this.scoreBoard);
            t.start();
            threads.add(t);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//Game END:
        for (Player player : players.values()) {
            CommonResult commonResult = new CommonResult().setFlag(CommonResult.Flag.BOARD)
                    .setResultData(this.scoreBoard);
            try {
                if(!player.getConn().isClosed())
                sendToClient(commonResult,player.getConn());
            } catch (IOException e) {
                System.out.println(player.toString()+"escape from game");
            }
        }
        this.scoreBoard.clear();
        room.getPlayers().clear();
        room.setStatus(GameRoom.RoomStatus.FREE);
        for (Player player : players.values()) {
            try {
                player.getConn().close(); //close connection
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("GamerLauncher "+this.getName()+" end");
    }

    public static void sendToClient(CommonResult result,Socket conn) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(conn.getOutputStream());
        objectOutputStream.writeObject(result);
        objectOutputStream.flush();
    }
    public static CommonResult recvFromClient(Socket conn) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
        CommonResult result =(CommonResult) objectInputStream.readObject();
        return result;
    }
}
