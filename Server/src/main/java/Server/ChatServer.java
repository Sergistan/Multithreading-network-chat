package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    public static CopyOnWriteArrayList<ServerThread> serverList = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(ChatServer.parsePort())) {
            System.out.println("Server Started");
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServerThread(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        }
    }

    private static int parsePort() {
        int port = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("Server/src/main/resources/settings.txt"))) {
            String str = br.readLine();
            String[] split = str.split("=");
            port = Integer.parseInt(split[1].trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }
}

