
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClientMain {
    private static String ip;
    private static int isTestMode=0;
    private static int port=6666;
    public static void sendToServer(CommonResult request,Socket conn) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(conn.getOutputStream());
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
    }
    public static CommonResult recvFromServer(Socket conn) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(conn.getInputStream());
        CommonResult result =(CommonResult) objectInputStream.readObject();
        return result;
    }

    public static int register(String playerName) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(ip, port);
        CommonResult request=new CommonResult();
        request.setFlag(CommonResult.Flag.REGIST);
        request.setResultData(playerName);
        sendToServer(request,socket);
        CommonResult result = recvFromServer(socket);
        int id = (int) result.getResultData();
        socket.close();
        return id;
    }
    public static String getRule()throws IOException, ClassNotFoundException {
        Socket socket = new Socket(ip, port);
        CommonResult request=new CommonResult();
        request.setFlag(CommonResult.Flag.RULE);
        sendToServer(request,socket);
        CommonResult result = recvFromServer(socket);
        socket.close();
        String rule=(String)result.getResultData();
        System.out.println(rule);
        return rule;
    }
    public static void quit(int id) throws IOException {
        Socket s=new Socket(ip,port);
        CommonResult request = new CommonResult();
        request.setFlag(CommonResult.Flag.Quit).setResultData(id);
        sendToServer(request,s);
    }
    public static void playGame(Socket longConn,int times) throws IOException, ClassNotFoundException {

        List<String> history = new ArrayList<>();
        while (times>0){
            String out = "You have "+ times +" guesses left.\n" +
                    "What is your next guess?\n" +
                    "Type in the characters for your guess and press enter.\n" +
                    "Enter guess: ";
            System.out.print(out);
            String input = InputUtility.readString(10);
            if("HISTORY".equals(input)){
                printHistory(history);
                continue;
            }
            Valid valid = new Valid();
            boolean flag = valid.check(input);
            boolean his = false;
            while(!flag){
                if("HISTORY".equals(input)){
                    printHistory(history);
                    his = true;
                    break;
                }
                System.out.println(input + " -> INVALID GUESS\n");
                System.out.print(out);
                input = InputUtility.readString(10);
                flag = valid.check(input);
            }
            if(his){
                continue;
            }
            //valid guess:

            CommonResult guess = new CommonResult().setFlag(CommonResult.Flag.GAMING);
            guess.setResultData(input);
            sendToServer(guess,longConn);
            CommonResult result = recvFromServer(longConn);
            List data= (List) result.getResultData();
            int black= (int) data.get(0);
            int white=(int)data.get(1);
            history.add(input + "\t\t" + black + "B_" + white + "W");
            if(result.getFlag()== CommonResult.Flag.GAME_END){
                System.out.println(input + " -> Result: " + black + "B_" + white + "W" + " – You guess right !!");
                break;
            }else {
                System.out.println(input + " -> Result: " + black + "B_" + white + "W\n");
            }
            times--;
        }
        if(times<=0){
            System.out.println("Sorry, you are out of guesses. You lose, boo-hoo.");
        }
        System.out.println("getting the scores from others.......");
        CommonResult result = recvFromServer(longConn);
        ScoreBoard board= (ScoreBoard) result.getResultData();
        System.out.println(board);
        String winer = board.getWiner();
        System.out.println(winer);
        System.out.print("Are you ready for another game (Y/N):");
        char input = InputUtility.readConfirmSelection();
        if(input=='N'){
            System.out.println("Game Over!");
            System.exit(0);
        }
        return;
    }
    public static void printHistory(List <String> history){
        System.out.println("guess\t\tcomputer’s reply");
        for(int i = history.size() - 1; i >= 0 ; i--){
            System.out.println(history.get(i));
        }
        System.out.println();
    }
    public static List<GameRoom> getGameRooms() throws IOException, ClassNotFoundException {
        Socket conn=new Socket(ip,port);
        CommonResult request = new CommonResult();
        request.setFlag(CommonResult.Flag.BROWSE);
        sendToServer(request,conn);
        CommonResult result = recvFromServer(conn);
        List<GameRoom> rooms=(List<GameRoom>)result.getResultData();
        conn.close();;
        return rooms;
    }
    public static void showRoomInfo(List<GameRoom> gameRooms){
        String roomInfo="";
        for (GameRoom gameRoom : gameRooms) {
            //RoomID RoomName Max
            roomInfo+="Room ID:"+gameRoom.getRoomId()+"\t\t"+"Room Name:"+gameRoom.getRoomName()+
                    "\t\t"+"Room Size:"+gameRoom.getMaxPlayerAmount()+"\t\t"+"playersNum:"+
                    gameRoom.getPresentPlayerNums()+"\t\t"+gameRoom.getStatus().getDescription()+"\n";
            roomInfo+="players in this room are:\n";
            Map<Integer,Player> playersInRoom=gameRoom.getPlayers();
            for (Player player : playersInRoom.values()) {
                roomInfo+=player.getPlayerName()+"\t\t";
            }
            roomInfo+="\n";
            roomInfo+="------------------------------------------------------------------------------------------------------------------\n";
        }
        System.out.println(roomInfo);
    }
    public static Socket joinInRoom(int roomId,int playerId) throws IOException, ClassNotFoundException {
        Socket longConn=new Socket(ip,port);
        CommonResult request = new CommonResult();
        LinkedList<Integer> joinInfo = new LinkedList<Integer>();
        joinInfo.add(roomId);
        joinInfo.add(playerId);
        request.setFlag(CommonResult.Flag.JOIN).setResultData(joinInfo);
        sendToServer(request,longConn);
        CommonResult result = recvFromServer(longConn);
        if(result.getFlag()== CommonResult.Flag.JOIN&&(int)result.getResultData()==1)
            return longConn;
        else
            return null;

    }
    public static void joinGame(int playerId) throws IOException, ClassNotFoundException {
        int select=-1;
        Socket longConn;
        while(true){
            List<GameRoom> gameRooms = getGameRooms();
            showRoomInfo(gameRooms);
            System.out.println("please select the room id [0-3] that you want to join in. e.g:2");
            select=Integer.parseInt(""+InputUtility.readMenuSelection());
            if(select<0||select>=gameRooms.size()){
                System.out.println("Invalid Input,please input a valid room id");
                continue;
            }
            else {
                longConn = joinInRoom(select, playerId);
                if(longConn!=null)
                    break;
                else {
                    System.out.println("sorry this room now is full, please select another\n");
                }
            }
        }

        System.out.println("Join room success");
        int maxTimes=0;
        String secretCode="";

        while(true){
            CommonResult result = recvFromServer(longConn);
            if(result.getFlag()== CommonResult.Flag.JOIN){
                System.out.println((String)result.getResultData());
                continue;
            }else if(result.getFlag()== CommonResult.Flag.GAME_START){
                List data=(List)result.getResultData();
                secretCode=(String)data.get(0);
                maxTimes=(int)data.get(1);
                System.out.println("Game starts .....");
                break;
            }
        }
        if(isTestMode==1){
            String out = "Generating secret code ...(for this example the secret code is "+ secretCode + ")\n";
            System.out.println(out);
        }
        else{
            System.out.println("Generating secret code ...\n");
        }
        playGame(longConn,maxTimes);
        longConn.close();
    }

    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("Usage: java ClientMain server_ip isTestMode");
            System.out.println("e.g: java ClientMain 192.168.1.5 1");
            return;
        }
        ip=args[0];
        isTestMode=Integer.parseInt(args[1]);
        try  {
            System.out.println("please input your player name (at most 20 characters):");
            String playerName = InputUtility.readString(20);
            int id = register(playerName);
            System.out.println("welcome "+playerName);
            while (true){
                System.out.println("please select the menu:");
                System.out.println("[1] GetRule\n[2] JoinGame\n[3] Quit");
                char c = InputUtility.readMenuSelection();
                switch (c){
                    case '1':
                        getRule();
                        break;
                    case '2':
                        joinGame(id);
                        break;
                    case '3':
                        quit(id);
                        return ;

                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
