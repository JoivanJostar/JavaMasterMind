import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class PlayGame extends Thread {
    Socket conn;
    String secretCode;
    ScoreBoard scoreBoard;
    Player player;

    public PlayGame(Player player,String secretCode,ScoreBoard scoreBoard){
        this.conn=player.getConn();
        this.player=player;
        this.secretCode=secretCode;
        this.scoreBoard=scoreBoard;
    }


    public void play() throws IOException, ClassNotFoundException {
        CommonResult result = new CommonResult().setFlag(CommonResult.Flag.GAME_START);
        LinkedList data = new LinkedList();
        int maxTimes=GameConfiguration.guessNumber;
        int times=maxTimes;
        data.add(this.secretCode);
        data.add(maxTimes);
        result.setResultData(data);
        long begin=System.currentTimeMillis();
        try {
            sendToClient(result,this.conn); //send start package: secretCode+guessTimes
            while (times>0){
                CommonResult request = recvFromClient(this.conn);
                String guess= (String) request.getResultData();
                times--;
                //game logic:
                int black = 0, white = 0;
                Set<Integer> set = new HashSet<>();
                for(int i = 0 ; i < secretCode.length(); i++){
                    if(secretCode.charAt(i) == guess.charAt(i)){
                        black++;
                        set.add(i);
                    }
                }
                for(int i = 0 ; i < guess.length(); i++){
                    for(int j = 0; j < secretCode.length(); j++){
                        if(guess.charAt(i) == secretCode.charAt(j) && i != j && !set.contains(j)){
                            white++;
                            set.add(j);
                        }
                    }
                }
                CommonResult guessResult = new CommonResult();
                if(black == 4){
                    guessResult.setFlag(CommonResult.Flag.GAME_END);
                    List datas=new LinkedList();
                    datas.add(black);
                    datas.add(white);
                    guessResult.setResultData(datas);
                    sendToClient(guessResult,this.conn);
                    break;
                }else {
                    guessResult.setFlag(CommonResult.Flag.GAMING);
                    List datas=new LinkedList();
                    datas.add(black);
                    datas.add(white);
                    guessResult.setResultData(datas);
                    sendToClient(guessResult,this.conn);
                }
            }

            //game end
            synchronized (this.scoreBoard){
                if(times>0){
                    this.scoreBoard.addItem(new ScoreBoardItem(this.player.getId(),this.player.getPlayerName(),
                            "Finish",maxTimes-times,(System.currentTimeMillis()-begin)/1000));
                }else{
                    this.scoreBoard.addItem(new ScoreBoardItem(this.player.getId(),this.player.getPlayerName(),
                            "Lost",maxTimes-times,(System.currentTimeMillis()-begin)/1000));
                }
            }
        }catch (IOException e){
            synchronized (this.scoreBoard){
                this.scoreBoard.addItem(new ScoreBoardItem(this.player.getId(),this.player.getPlayerName(),
                        "Escape",maxTimes-times,(System.currentTimeMillis()-begin)/1000));
            }
            player.getConn().close();
            System.out.println(this.player.toString()+" escape from game");
            return;
        }

    }



    @Override
    public void run() {
        try {
            play();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
