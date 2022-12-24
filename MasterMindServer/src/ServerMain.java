import java.net.Socket;

public class ServerMain {

    public static void main(String[] args) throws InterruptedException {

        GameHall gameHall = new GameHall();
        gameHall.start();
        gameHall.join();
        System.out.println("Server exit\n");
    }
}
