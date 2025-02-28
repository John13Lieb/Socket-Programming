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
        Scanner scanner = new Scanner(System.in);
        String[] runs = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh"};

        /** ADD FOR LOOP THAT RUNS THE FOLLOWING 7 TIMES **/
        for (int i = 0; i < 7; i++) {
            byte[] byteBuffer = new byte[2];
            System.out.print("Enter " + runs[i] + " short in the range (-32,768 to 32,767): ");

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
            String numStr = Short.toString(val);
            int recvMsgSize = numStr.length() * 2 + 2;
            byte[] recvBuffer = new byte[recvMsgSize];

            int totalBytesRcvd = 0; // Total bytes received so far
            int bytesRcvd; // Bytes received in last read

            while (totalBytesRcvd < recvBuffer.length) {
                if ((bytesRcvd = in.read(recvBuffer, totalBytesRcvd,
                        recvBuffer.length - totalBytesRcvd)) == -1)
                    throw new SocketException("Connection close prematurely");
                totalBytesRcvd += bytesRcvd;
            }
            long endTime = System.currentTimeMillis();
            long runTime = endTime - startTime;
            runTimes[i] = runTime;

            System.out.print("Received Bytes: ");
            for (byte r : recvBuffer) {
                System.out.printf("0x%02X ", r);
            }
            System.out.println("\nDuration: " + runTime + " ms\n");

            socket.close(); // Close the socket and its streams
        }
        scanner.close();

        /** AFTER 7 RUNS, REPORT MIN, MAX, AND AVERAGE RUNTIME **/
        long min = runTimes[0];
        long max = runTimes[0];
        long sum = runTimes[0];

        for (int i = 1; i < runTimes.length; i++) {
            if (runTimes[i] < min) {
                min = runTimes[i];
            }
            if (runTimes[i] > max) {
                max = runTimes[i];
            }
            sum += runTimes[i];
        }
        long avg = sum / runTimes.length;

        String output = "Seven Number Round-Trip Statistics: \n" +
                "Minimum: " + min + " ms\n" +
                "Maximum: " + max + " ms\n" +
                "Average: " + avg + " ms";
        System.out.println(output + "\n");
    }
}