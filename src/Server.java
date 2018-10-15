import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server extends Thread {
    public static void main(String[] args) {
        System.out.println("=============SERVER==============");

        final int PORT_LISTEN = 5656;

        List<User> users = new ArrayList<User>();

        try (ServerSocket server = new ServerSocket(PORT_LISTEN)) {
            System.out.println("* Server starting at " + PORT_LISTEN );

            while (true) {


                System.out.println("* Waiting for next client");
                Socket socket = server.accept();
                System.out.println("* Client connected");

                String clientIp = socket.getInetAddress().getHostAddress();
                System.out.println("* IP: " + clientIp);
                System.out.println("* PORT: " + socket.getPort());

                new ServerThread(socket, users).start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}