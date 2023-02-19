package Client;

import java.io.*;

public class ChatClient {
    public static String ipAddr = "localhost";

    public static void main(String[] args) {
        new ClientThread(ipAddr,ChatClient.parsePort());
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

