package ChatClient;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
    private static int PORT;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("Server/src/main/resources/settings.txt"))) {
            String str = br.readLine();
            String[] split = str.split("=");
            PORT = Integer.parseInt(split[1].trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket("localhost", PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {

            System.out.println("Соединение с сервером установлено.");
            String massageFromServer = in.readLine();
            System.out.println(massageFromServer);

            String name = scanner.nextLine();
            out.println(name + "\n");
            out.flush();

            System.out.println(name + " можете общаться");

            while (!socket.isClosed())
            {
                String text = scanner.nextLine();
                out.println(text);
                out.flush();

                if (text.equals("/exit")) {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

