package Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
                    for (ServerThread vr : ChatServer.serverList) {
                        vr.noticeExitUser(name);
                    }
                    this.downService();
                    break;
                }
                for (ServerThread vr : ChatServer.serverList) {
                    vr.writeLog(name, message);
                }
            }
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            this.downService();
        }
    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerThread vr : ChatServer.serverList) {
                    if(vr.equals(this)){
                        vr.interrupt();
                    }
                    ChatServer.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }


    private void writeYourName() {
        try {
            System.out.println("(" + dataTime + ") " + "Перед тем как начать общаться напишите ваше имя!");
            out.write("(" + dataTime + ") " + "Перед тем как начать общаться напишите ваше имя!" + "\n");
            outLog.write("(" + dataTime + ") " + "Перед тем как начать общаться напишите ваше имя!" + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }

    private void noticeNewUser(String name) {
        try {
            System.out.println("(" + dataTime + ") " + "В чат присоединился: " + name);
            out.write("(" + dataTime + ") " + "В чат присоединился: " + name + "\n");
            outLog.write("(" + dataTime + ") " + "В чат присоединился: " + name + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }

    private void noticeExitUser(String name) {
        try {
            System.out.println("(" + dataTime + ") " + name + ": " + "вышел из чата");
            out.write("(" + dataTime + ") " + name + ": " + "вышел из чата" + "\n");
            outLog.write("(" + dataTime + ") " + name + ": " + "вышел из чата" + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }

    private void writeLog(String name, String message) {
        try {
            System.out.println("(" + dataTime + ") " + name + ": " + message);
            out.write("(" + dataTime + ") " + name + ": " + message + "\n");
            outLog.write("(" + dataTime + ") " + name + ": " + message + "\n");
            out.flush();
            outLog.flush();
        } catch (IOException ignored) {
        }
    }
}
