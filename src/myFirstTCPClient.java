import java.net.*; // for Socket
import java.io.*; // for IOException and Input/OutputStream
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class myFirstTCPClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> [<Port>]");

        String server = args[0]; // Server name or IP address
        int servPort = Integer.parseInt(args[1]);

// Create socket that is connected to server on specified port
        Socket socket = new Socket(server, servPort);
        System.out.println("Server connection established");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

// [ADD COMMENT HERE]
        byte[] byteBuffer = new byte[2];
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a short in the range (-32,768 to 32,767): ");

        short val;
        try {
            val = scanner.nextShort();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a short value in the range (-32,768 to 32,767): ");
            val = scanner.nextShort();
        }

        scanner.close();

// Convert input String to bytes using the default character encoding
        ByteBuffer.wrap(byteBuffer).order(ByteOrder.BIG_ENDIAN).putShort(val); // convert short to byte array
        for (byte b : byteBuffer) {
            System.out.printf("0x%02X ", b);
        }
        System.out.println();

        out.write(byteBuffer); // Send the encoded string to the server
// Receive the same string back from the server
        int totalBytesRcvd = 0; // Total bytes received so far
        int bytesRcvd; // Bytes received in last read
        while (totalBytesRcvd < byteBuffer.length) {
            if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
                    byteBuffer.length - totalBytesRcvd)) == -1)
                throw new SocketException("Connection close prematurely");
            totalBytesRcvd += bytesRcvd;
        }
        System.out.println("Received: " + new String(byteBuffer));
        socket.close(); // Close the socket and its streams
    }
}