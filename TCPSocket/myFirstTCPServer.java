package TCPSocket;

import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class myFirstTCPServer {
    private static final int BUFSIZE = 2; // Size of receive buffer
    public static void main(String[] args) throws IOException {
        if (args.length != 1) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Port>");

        int servPort = Integer.parseInt(args[0]);

        // Create a server socket to accept client connection requests
        ServerSocket servSock = new ServerSocket(servPort);

        for (;;) { // Run forever, accepting and servicing connections
            Socket clntSock = servSock.accept(); // Get client connection
            System.out.println("Handling client at " +
                    clntSock.getInetAddress().getHostAddress() + " on port " +
                    clntSock.getPort());
            InputStream in = clntSock.getInputStream();
            OutputStream out = clntSock.getOutputStream();

            byte[] byteBuffer = new byte[BUFSIZE]; // Receive buffer
            int recvMsgSize = in.read(byteBuffer); // Size of received message

            if (recvMsgSize != 2) {
                // Send error message back to client if message size is not 2 bytes
                String errorMessage = "****";
                byte[] errorBuffer = errorMessage.getBytes(StandardCharsets.UTF_16);
                System.out.print("\nSending error message: ");
                for (byte b : errorBuffer) {
                    System.out.printf("0x%02X ", b);
                }
                out.write(errorBuffer); // Send error message to client
            }
            else {
                System.out.print("\nReceived Bytes: ");
                for (byte b : byteBuffer) {
                    System.out.printf("0x%02X ", b);
                }

                // Convert received bytes to short
                short num = ByteBuffer.wrap(byteBuffer, 0, 2).order(ByteOrder.BIG_ENDIAN).getShort();
                System.out.println("\nReceived Number: " + num);

                // Convert short to string and send back to client as UTF-16 byte array
                String numStr = Short.toString(num);
                byte[] sendBuffer = numStr.getBytes(StandardCharsets.UTF_16);

                System.out.print("Sending Bytes: ");
                for (byte b : sendBuffer) {
                    System.out.printf("0x%02X ", b);
                }
                System.out.println("\n");

                out.write(sendBuffer); // Send the encoded string back to the client
            }

            clntSock.close(); // Close the socket. We are done with this client!
        }
        /* NOT REACHED */
    }
}