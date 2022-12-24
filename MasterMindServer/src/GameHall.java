import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameHall extends Thread{
    Map<Integer,Player> playerMap;
    List<GameRoom> gameRooms;

    public  GameHall(){
        gameRooms =new LinkedList<GameRoom>();
        this.playerMap=new HashMap<>();
        GameRoom room0=new GameRoom("RemoteGameRoom0",0,1);
        GameRoom room1=new GameRoom("RemoteGameRoom1",1,2);
        GameRoom room2=new GameRoom("RemoteGameRoom2",2,3);
        GameRoom room3=new GameRoom("RemoteGameRoom3",3,4);
        gameRooms.add(room0);
        gameRooms.add(room1);
        gameRooms.add(room2);
        gameRooms.add(room3);
    }

    public void browseRoom(Socket conn) throws IOException {

        CommonResult result = new CommonResult();
        synchronized (gameRooms){
            result.setFlag(CommonResult.Flag.BROWSE).setResultData(this.gameRooms);
        }
        sendToClient(result,conn);
    }
    public void startGame(GameRoom room) throws IOException {
        CommonResult result = new CommonResult();
        result.setFlag(CommonResult.Flag.GAMING);
        room.setStatus(GameRoom.RoomStatus.GAMING);
        for (Player player : room.getPlayers().values()) {
            Socket conn = player.getConn();
            sendToClient(result,conn);
        }
        GamerLauncher gamerLauncher =new GamerLauncher(room.getPlayers(),room);
        gamerLauncher.start();

    }
    public void joinInRoom(CommonResult request,Socket conn) throws IOException {
        List<Integer> data =(List<Integer>) request.getResultData();
        int roomID=data.get(0);
        int playerId=data.get(1);
        Player player;
        GameRoom gameRoom;
        synchronized (playerMap){
            player = playerMap.get(playerId);
            player.setConn(conn);
        }

        synchronized (gameRooms) {
            gameRoom = gameRooms.get(roomID);
        }
        if(gameRoom.getStatus()== GameRoom.RoomStatus.GAMING || gameRoom.getPresentPlayerNums()>=gameRoom.getMaxPlayerAmount()){
            CommonResult result = new CommonResult().setResultData(CommonResult.Flag.JOIN).setResultData(0);
            try {
                sendToClient(result,conn);
            }catch (IOException e){
                e.printStackTrace();
            }
            return; //join fail
        }else{
            synchronized (gameRoom){
                gameRoom.addPlayer(player); //join in
            }
            CommonResult result = new CommonResult().setFlag(CommonResult.Flag.JOIN).setResultData(1);
            try {
                sendToClient(result,conn);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        // join success
        CommonResult result = new CommonResult();
        result.setFlag(CommonResult.Flag.JOIN).setResultData("Player "+player.getPlayerName()+
                " Join in the room "+gameRoom.getRoomName()+", waiting other players to join in......\n");
        synchronized (gameRoom.getPlayers()){
            Collection<Player> values = gameRoom.getPlayers().values();
            Iterator<Player> iterator = values.iterator();
            while(iterator.hasNext()){
                Player next = iterator.next();
                try {
                    sendToClient(result,next.getConn());
                }catch (IOException e){
                    // this player is offline
                    next.getConn().close();
                    System.out.println(next.toString()+" out of room "+gameRoom.getRoomId());
                    iterator.remove();
                }
            }
        }
        if(gameRoom.getMaxPlayerAmount()==gameRoom.getPresentPlayerNums()){
            startGame(gameRoom);
        }
    }
    public  Player registPlayer(CommonResult request,Socket conn) throws IOException {
        String playerName=(String)request.getResultData();
        InetAddress clientInetAddr = conn.getInetAddress();
        String clientHostAddress = clientInetAddr.getHostAddress();
        int clientPort = conn.getPort();
        Player player= new Player(playerName,clientHostAddress,clientPort);
        CommonResult result = new CommonResult();
        result.setFlag(CommonResult.Flag.REGIST);
        result.setResultData(player.getId());
        sendToClient(result,conn);
        synchronized (this.playerMap){
            playerMap.put(player.getId(),player);
        }
        System.out.println("a player has been registered:");
        System.out.println(player);
        return player;
    }
    public void sendRule(CommonResult request,Socket conn) throws IOException {
        String rules = Rules.getRules();
        CommonResult result = new CommonResult();
        result.setFlag(CommonResult.Flag.RULE);
        result.setResultData(rules);
        sendToClient(result,conn);
    }
    public void clientQuit(CommonResult request,Socket conn)throws IOException{
        int id= (int)request.getResultData();
        synchronized (this.playerMap){
            Player player = playerMap.get(id);
            System.out.println("a player has quited:");
            System.out.println(player);
            playerMap.remove(id);
        }

    }
    @Override
    public void run() {
        System.out.println("Server start on port "+6666);
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            while(true){
                Socket clientConn = serverSocket.accept();
                try {
                    CommonResult requset = recvFromClient(clientConn);
                    CommonResult.Flag flag = requset.getFlag();
                    System.out.println(flag);
                    switch (flag) {
                        case BROWSE:
                            browseRoom(clientConn);
                            clientConn.close();
                            break;
                        case JOIN:
                            joinInRoom(requset,clientConn);
                            break;
                        case REGIST:
                            registPlayer(requset, clientConn);
                            clientConn.close();
                            break;
                        case RULE:
                            sendRule(requset,clientConn);
                            clientConn.close();
                            break;
                        case Quit:
                            clientQuit(requset,clientConn);
                            clientConn.close();
                            break;

                    }
                }catch (IOException | ClassNotFoundException e){
                    System.out.println("disconnect with "+" "+clientConn.getInetAddress().getHostAddress());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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
