package ChatServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatServer {

    private static int PORT;
    private static ArrayList<Thread> threads = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("Server/src/main/resources/settings.txt"))) {
            String str = br.readLine();
            String[] split = str.split("=");
            PORT = Integer.parseInt(split[1].trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Чат в ожидании.");
            Socket clientSocket = serverSocket.accept();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
                 BufferedWriter outLog = new BufferedWriter(new FileWriter("File.log", true))) {

                Thread thread = new Thread(()->{
                    out.write("Перед тем как начать общаться напишите ваше имя!" + "\n");  ////////////////////////////////////////
                    out.flush();

                    String name = null;
                    try {
                        name = in.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("В чат присоединился: " + name);
                    try {
                        outLog.write("В чат присоединился: " + name + " " +  LocalDateTime.now() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (!clientSocket.isClosed()) {
                        String massageFromUser = null;
                        try {
                            massageFromUser = in.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!massageFromUser.isEmpty()) {
                            if (massageFromUser.equals("/exit")) {
                                System.out.println("Client initialize connections suicide ...");
                                out.write("Server reply - " + massageFromUser + " - OK");
                                out.flush();
                                break;
                            }
                            try {
                                outLog.write(name + ": " +  " " + massageFromUser + " " + LocalDateTime.now() + "\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outLog.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }                                                                           ///////////////////////////////////////////////
                });
                threads.add(thread);

                for (Thread t: threads) {
                    t.join();
                }


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

