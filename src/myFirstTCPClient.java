import java.net.*; // for Socket
import java.io.*; // for IOException and Input/OutputStream
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class myFirstTCPClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> [<Port>]");

        double[] runTimes = new double[7]; // Array to store round-trip times
        Scanner scanner = new Scanner(System.in);
        String[] runs = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh"};

        for (int i = 0; i < 7; i++) {
            byte[] byteBuffer = new byte[2];
            System.out.print("Enter " + runs[i] + " short in the range (-32,768 to 32,767): ");

            short val;
            while (true) {
                try {
                    val = scanner.nextShort(); // Read short from user
                    break;
                } catch (Exception e) {
                    System.out.print("Invalid input. Please enter a short value in the range (-32,768 to 32,767): ");
                    scanner.next(); // Clear invalid input
                }
            }

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

            double startTime = System.nanoTime();
            out.write(byteBuffer); // Send the encoded string to the server

            String numStr = Short.toString(val);
            int recvMsgSize = numStr.length() * 2 + 2; // Size of received message (UTF-16)
            byte[] recvBuffer = new byte[recvMsgSize];

            int totalBytesRcvd = 0; // Total bytes received so far
            int bytesRcvd; // Bytes received in last read

            // Read from server until buffer is full
            while (totalBytesRcvd < recvBuffer.length) {
                if ((bytesRcvd = in.read(recvBuffer, totalBytesRcvd,
                        recvBuffer.length - totalBytesRcvd)) == -1)
                    throw new SocketException("Connection close prematurely");
                totalBytesRcvd += bytesRcvd;
            }

            double endTime = System.nanoTime();
            double runTime = (endTime - startTime) / 1_000_000; // Calculate trip time (ms)
            runTimes[i] = runTime;

            System.out.print("Received Bytes: ");
            for (byte r : recvBuffer) {
                System.out.printf("0x%02X ", r);
            }
            System.out.printf("\nDuration: %.2f ms\n", runTime);

            socket.close(); // Close the socket and its streams
        }
        scanner.close();

        // Compute round-trip statistics
        double min = runTimes[0];
        double max = runTimes[0];
        double sum = runTimes[0];

        for (int i = 1; i < runTimes.length; i++) {
            if (runTimes[i] < min) {
                min = runTimes[i];
            }
            if (runTimes[i] > max) {
                max = runTimes[i];
            }
            sum += runTimes[i];
        }
        double avg = sum / runTimes.length;

        String output = String.format(
                "%nSeven Number Round-Trip Statistics:%n" +
                        "Minimum: %.2f ms%n" +
                        "Maximum: %.2f ms%n" +
                        "Average: %.2f ms%n",
                min, max, avg
        );
        System.out.println(output);
    }
}