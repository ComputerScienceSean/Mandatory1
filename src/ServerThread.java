import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerThread extends Thread {
    private Socket socket;
    private List<User> users;
    private String name;
    private InputStream inFromUser;
    private PrintWriter pw;

    public ServerThread(Socket socket, List<User> users) {
        this.socket = socket;
        this.users = users;
    }

    public void run() {
        User u = null;
        try {
            inFromUser = socket.getInputStream();

            byte[] data = new byte[1024];
            inFromUser.read(data);
            String joinMsg = new String(data);

            if (joinMsg.startsWith("JOIN ")) {
                boolean ok = true;
                int p = joinMsg.indexOf(",");
                if (p > 0) {
                    name = joinMsg.substring(5, p);
                    if (name.length() > 12 || !name.matches("[A-Z-ÆØÅa-zæøå0-9_-]+")) {
                        write("J_ER 321: Invalid characters used in username");
                        ok = false;
                    }

                } else
                    ok = false;


                if (ok == true) {

                    boolean check = true;
                    for (User user : users) {
                        System.out.println(user.getName());
                        System.out.println(name);
                        if (user.getName().equalsIgnoreCase(name)) {
                            write("J_ER 666: Username already exists");
                            check = false;
                        }
                    }
                    // User accepted
                    if (check == true) {
                        u = new User();
                        u.setName(name);
                        u.setSocket(socket);
                        users.add(u);
                        System.out.println("* " + name + " Just joined\n* Here's a list of present users" + users.toString());
                        write("J_OK");
                    }


                    boolean shouldRun = true;
                    while (shouldRun) {
                        InputStream inputStream = socket.getInputStream();
                        byte[] data1 = new byte[1024];
                        inputStream.read(data1);
                        String input = new String(data1).trim();

                        if (input.length() == 0) {
                            shouldRun = false;
                        } else {
                            String receivedMsg = "DATA " + name + ": " + input;
                            System.out.println(receivedMsg);
                            // write(name + " said: " + input);
                            writeToClients(receivedMsg, u);


                        }
                    }
                }
            }

            System.out.println("* " + name + " Disconnected");
            System.out.println("* List of present users" + users);
            for (int i = 0; i >= users.size(); i++) {
                if (users.get(i).getName().equals(name)) {
                    users.remove(i);
                    break;
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();

        } finally {

            try {
                socket.close();
            } catch (IOException e) {
            }
        }


    }

    public String writeToClients(String receivedMsg, User user) throws IOException {
        if (receivedMsg.startsWith("DATA ")) {
            int check = receivedMsg.indexOf(':');
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).getName().equals(user.getName())) {
                    OutputStream output = users.get(i).getSocket().getOutputStream();
                    //write(receivedMsg);
                    output.write(receivedMsg.getBytes());
                }
            }
        }
        return receivedMsg;
    }


    public void write(String msgToSend) {
        try {
            OutputStream writeMsg = socket.getOutputStream();
            writeMsg.write(msgToSend.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
