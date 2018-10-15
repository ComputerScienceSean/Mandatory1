import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Runnable {

    private static Socket socket;
    private static boolean shouldPingRun = true;

    public static void main(String[] args) {


        serverConnection(args);
    }


    public static void serverConnection(String[] args) {
        System.out.println("=============CLIENT==============");

        Scanner sc = new Scanner(System.in);
        System.out.print("What is the IP for the server (type 0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : sc.nextLine();

        System.out.print("What is the PORT for the server: ");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : sc.nextInt();


        final int PORT_SERVER = portToConnect;
        final String IP_SERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;


        try {
            InetAddress ip = InetAddress.getByName(IP_SERVER_STR);
            System.out.println("\nConnecting...");
            System.out.println("SERVER IP: " + IP_SERVER_STR);
            System.out.println("SERVER PORT: " + PORT_SERVER + "\n");

            socket = new Socket(ip, PORT_SERVER);
            sc = new Scanner(System.in);


            System.out.println("Enter Name");
            String name = sc.nextLine();
            write("JOIN " + name + ", " + ipToConnect + ":" + portToConnect);


            try {
                InputStream read = socket.getInputStream();
                byte[] dataIn = new byte[1024];
                read.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();

                if (msgIn.startsWith("J_ER ")){
                    System.out.println("J_ER received. Restarting server.");
                    serverConnection(args);
                }

                if (msgIn.startsWith("DATA ")){
                    System.out.println(msgIn.substring(5));
                }

                if (msgIn.equals("J_OK")) {
                    new Thread(() -> {
                        while (true) {
                            try {
                                InputStream readClient = socket.getInputStream();
                                byte[] dataInClient = new byte[1024];
                                readClient.read(dataInClient);
                                String msgInClient = new String(dataInClient);
                                msgInClient = msgInClient.trim();
                                if (!msgInClient.equals("")) {
                                    System.out.println(msgInClient);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();

                    boolean shouldChatRun = true;
                    while (shouldChatRun) {
                        System.out.println("What is your message?");
                        String line = sc.nextLine();
                        String lineToSend = "DATA " + name + ": " + line;

                        if (line.equalsIgnoreCase("quit")) {
                            quitMsg();
                            shouldChatRun = false;
                        }

                         else if (line.length() == 0) {
                            shouldChatRun = false;
                        } else {
                            write(lineToSend);
                        }
                    }


                }
                System.out.println(msgIn);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void quitMsg(){
        OutputStream output = null;
        try {
            output = socket.getOutputStream();
            output.write("QUIT".getBytes());
            output.flush();
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void write(String msgToSend) throws IOException {
        synchronized (socket) {
            OutputStream output = socket.getOutputStream();
            output.write(msgToSend.getBytes());
            output.flush();

            while (msgToSend.length() > 250) {
                System.out.println("Message too long, try again");
                break;
            }
        }
    }

    public void run() {
        shouldPingRun = true;
        while (shouldPingRun) {
            try {
                synchronized (this) {
                    wait(60 * 1000);
                }
                if (shouldPingRun)
                    write("I'm alive");
            } catch (InterruptedException e) {
                shouldPingRun = false;
            } catch (IOException ex) {
                shouldPingRun = false;
            }
        }
        System.out.println("Client IMAV stopped");
    }



}