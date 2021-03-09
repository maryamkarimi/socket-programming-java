import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) throws IOException {

        final String INPUT_PROMPT = "Input    > ";
        final String RECEIVED_PROMPT = "Received > ";
        Socket server = null;
        PrintWriter toServer = null;
        BufferedReader fromServer = null;

        try {
            if (args.length != 2) {
                System.out.println("usage: java client.ChatClient <compname> <port>");
                System.exit(0);
            }
            final String computer = args[0];
            final int port = Integer.parseInt(args[1]);

            server = new Socket(computer, port);
            toServer = new PrintWriter(server.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));

            final BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String buff;

            System.out.println("Chat connection estabished.");
            System.out.print(INPUT_PROMPT);

            while ((buff = stdIn.readLine()) != null) {
                final String fileName = buff;

                // Check that the input includes the extension .txt
                if (!buff.matches(".+\\.txt")) {
                    System.out.println("The name of the file must include the extension \".txt\"");
                    System.out.print(INPUT_PROMPT);
                    continue;
                }

                toServer.println(buff);
                if (buff.equals("bye")) break;

                buff = fromServer.readLine();
                if (buff.equals("FOUND")) {
                    // create file to write to
                    BufferedWriter bw = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(new File(fileName)))
                    );

                    buff = fromServer.readLine();

                    while (!buff.equals("END")) {
                        bw.write(buff);
                        bw.newLine();
                        buff = fromServer.readLine();
                    }

                    System.out.println("Download Completed");
                    bw.close();
                    break;
                }

                System.out.print(RECEIVED_PROMPT);
                System.out.println(buff);
                System.out.print(INPUT_PROMPT);
            }

            System.out.println("Chat Connection Closed.");
            toServer.close();
            fromServer.close();
            stdIn.close();
            server.close();

        } catch (UnknownHostException e) {
            System.err.println("Cannot connect to host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO Error");
            System.exit(1);
        }

    }
}
