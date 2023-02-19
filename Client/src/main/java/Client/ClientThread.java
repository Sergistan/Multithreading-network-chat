package Client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientThread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader inputUser;
    private String host;
    private int port;
    private String nickname;

    public ClientThread(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            this.socket = new Socket(host, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            new ReadMsg().start(); // нить читающая сообщения из сокета в бесконечном цикле
            this.pressNickname();
            new WriteMsg().start(); // нить пишущая сообщения в сокет приходящие с консоли в бесконечном цикле
        } catch (IOException e) {
            ClientThread.this.downService();
        }
    }

        private void pressNickname() {
            try {
                nickname = inputUser.readLine();
                out.write(nickname + "\n");
                out.flush();
            } catch (IOException ignored) {
            }
        }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine(); // ждем сообщения с сервера
                    if (str.equals("/exit")) {
                        ClientThread.this.downService(); // харакири
                        break; // выходим из цикла если пришло "stop"
                    }
                    System.out.println(str); // пишем сообщение с сервера на консоль
                }
            } catch (IOException e) {
                ClientThread.this.downService();
            }
        }
    }

    // нить отправляющая сообщения приходящие с консоли на сервер
    public class WriteMsg extends Thread {

        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = inputUser.readLine(); // сообщения с консоли
                    if (userWord.equals("/exit")) {
                        out.write("/exit");
                        ClientThread.this.downService(); // харакири
                        break; // выходим из цикла если пришло "stop"
                    } else {
                        out.write(userWord + "\n"); // отправляем на сервер
                    }
                    out.flush(); // чистим
                } catch (IOException e) {
                    ClientThread.this.downService(); // в случае исключения тоже харакири
                }

            }
        }
    }

}
