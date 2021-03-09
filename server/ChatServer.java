import java.net.*;
import java.io.*;

public class ChatServer {
    public static void main(String[] args) throws IOException {
        final String RECEIVED_PROMPT = "Received > ";

        try {
            if (args.length != 1) {
                System.out.println("usage: java server.ChatServer <port>");
                System.exit(0);
            }

            final int port = Integer.parseInt(args[0]);
            System.out.println("Chat Server Waiting For Connection.");

            final ServerSocket serverSocket = new ServerSocket(port);

            final Socket clientSocket = serverSocket.accept();

            final PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);
            final BufferedReader fromClient = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            String inputLine;

            System.out.println("Chat Connection Established.");

            while ((inputLine = fromClient.readLine()) != null) {
                System.out.print(RECEIVED_PROMPT);
                System.out.println(inputLine);

                if (inputLine.equals("bye")) {
                    toClient.println("bye");
                    break;
                } else {
                    try {
                        final BufferedReader reader = new BufferedReader(new FileReader(inputLine));
                        System.out.println("File Found... Sending to Client");

                        toClient.println("FOUND");

                        String line;
                        while ((line = reader.readLine()) != null) {
                            toClient.println(line);
                        }

                        toClient.println("END");
                        reader.close();

                    } catch (FileNotFoundException e) {
                        System.out.println("File Not Found.");
                        toClient.println("END");
                    }
                }
            }

            toClient.close();
            fromClient.close();
            clientSocket.close();
            serverSocket.close();
            System.out.println("Chat Connection Closed");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
