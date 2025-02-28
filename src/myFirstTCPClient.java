import java.net.*; // for Socket
import java.io.*; // for IOException and Input/OutputStream
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class myFirstTCPClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> [<Port>]");

// Create a byte array to hold the short value
        long[] runTimes = new long[7];

        /** ADD FOR LOOP THAT RUNS THE FOLLOWING 7 TIMES **/
        byte[] byteBuffer = new byte[2];
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a short in the range (-32,768 to 32,767): ");

        short val;
        while (true) {
            try {
                val = scanner.nextShort();
                break;
            } catch (Exception e) {
                System.out.print("Invalid input. Please enter a short value in the range (-32,768 to 32,767): ");
                scanner.next();
            }
        }

// Convert input String to bytes using the default character encoding
        ByteBuffer.wrap(byteBuffer).order(ByteOrder.BIG_ENDIAN).putShort(val); // convert short to byte array
        System.out.print("\nSending Bytes: ");
        for (byte b : byteBuffer) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println();

        String server = args[0]; // Server name or IP address
        int servPort = Integer.parseInt(args[1]);

// Create socket that is connected to server on specified port
        Socket socket = new Socket(server, servPort);
        System.out.println("Connected to server...sending echo string");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        long startTime = System.currentTimeMillis();
        out.write(byteBuffer); // Send the encoded string to the server

// Receive the same string back from the server

        // make a receive buffer that has a length of short.toString().length() * 2 + 2

        int totalBytesRcvd = 0; // Total bytes received so far
        int bytesRcvd; // Bytes received in last read
        while (totalBytesRcvd < byteBuffer.length) {
            if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
                    byteBuffer.length - totalBytesRcvd)) == -1)
                throw new SocketException("Connection close prematurely");
            totalBytesRcvd += bytesRcvd;
        }
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;

        System.out.println("Received Bytes: " + new String(byteBuffer));
        System.out.println("Duration: " + runTime + " ms\n");

        /** AFTER 7 RUNS, REPORT MIN, MAX, AND AVERAGE RUNTIME **/

        scanner.close();
        socket.close(); // Close the socket and its streams
    }
}