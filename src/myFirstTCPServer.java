import java.net.*; // for Socket, ServerSocket, and InetAddress
import java.io.*; // for IOException and Input/OutputStream
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

            System.out.print("\nReceived Bytes: ");
            for (byte b : byteBuffer) {
                System.out.printf("0x%02X ", b);
            }

            short num = ByteBuffer.wrap(byteBuffer, 0, 2).order(ByteOrder.BIG_ENDIAN).getShort();
            System.out.println("\nReceived Number: " + num);

            clntSock.close();

// Receive until client closes connection, indicated by -1 return
//            while ((recvMsgSize = in.read(byteBuffer)) != -1)
//                out.write(byteBuffer, 0, recvMsgSize);
//            clntSock.close(); // Close the socket. We are done with this client!
        }
        /* NOT REACHED */
    }
}