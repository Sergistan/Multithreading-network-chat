package Server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;

public class ServerThread extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final BufferedWriter outLog;
    private final Date currentTime = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    private final String dataTime = dateFormat.format(currentTime);

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        outLog = new BufferedWriter(new FileWriter("File.log", true));
        this.start();
    }

    @Override
    public void run() {
        String name;
        String message;
        try {
            this.writeYourName();
            name = in.readLine();
            this.noticeNewUser(name);
            while (true) {
                message = in.readLine();
                if (message.equals("/exit")) {
                    System.out.println("(" + dataTime + ") " + name + ": " + "left the chat");
                    outLog.write("(" + dataTime + ") " + name + ": " + "left the chat" + "\n");
                    for (ServerThread connection : ChatServer.serverList) {
                        if (!connection.equals(this))
                            connection.noticeExitUser(name);
                    }
                    this.downService();
                    break;
                }
                System.out.println("(" + dataTime + ") " + name + ": " + message);
                outLog.write("(" + dataTime + ") " + name + ": " + message + "\n");
                outLog.flush();
                for (ServerThread connection : ChatServer.serverList) {
                    connection.writeToUsers(name, message);
                }
            }
        } catch (IOException e) {
            this.downService();
        }
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                in.close();
                out.close();
                outLog.close();
                socket.close();
                for (ServerThread connection : ChatServer.serverList) {
                    if(connection.equals(this))
                        connection.interrupt();
                    }
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }


    private void writeYourName() {
        try {
            System.out.println("(" + dataTime + ") " + "Write your name!");
            out.write("(" + dataTime + ") " + "Write your name!" + "\n");
            outLog.write("(" + dataTime + ") " + "Write your name!" + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }

    private void noticeNewUser(String name) {
        try {
            System.out.println("(" + dataTime + ") " + name + " joined the chat. Write something...");
            out.write("(" + dataTime + ") " + name + " joined the chat. Write something..." + "\n");
            outLog.write("(" + dataTime + ") " + name + " joined the chat. Write something..." + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }

    private void noticeExitUser(String name) {
        try {
            out.write("(" + dataTime + ") " + name + ": " + "left the chat" + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void writeToUsers(String name, String message) {
        try {
            out.write("(" + dataTime + ") " + name + ": " + message + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }
}
