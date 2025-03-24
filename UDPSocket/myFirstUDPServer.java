package UDPSocket;

import java.net.*; // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*; // for IOException
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class myFirstUDPServer {

    private static final int ECHOMAX = 2; // Maximum size of echo datagram

    public static void main(String[] args) throws IOException {
        if (args.length != 1) // Test for correct argument list
            throw new IllegalArgumentException("Parameter(s): <Port>");

        int servPort = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket(servPort);
        DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

        for (;;) { // Run forever, receiving and echoing datagrams
            socket.receive(packet); // Receive packet from client
            System.out.println("Handling client at " +
                    packet.getAddress().getHostAddress() + " on port " + packet.getPort());

            int recvMsgSize = packet.getLength(); // Size of received message
            byte[] byteBuffer = packet.getData();

            if (recvMsgSize != 2) {
                // Send error message back to client if message size is not 2 bytes
                String errorMessage = "****";
                byte[] errorBuffer = errorMessage.getBytes(StandardCharsets.UTF_16);
                System.out.print("\nSending error message: ");
                for (byte b : errorBuffer) {
                    System.out.printf("0x%02X ", b);
                }
                DatagramPacket errorPacket = new DatagramPacket(errorBuffer, errorBuffer.length,
                        packet.getAddress(), packet.getPort());
                socket.send(errorPacket); // Send error message to client
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

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                        packet.getAddress(), packet.getPort());
                socket.send(sendPacket); // Send the same packet back to client
            }

            packet.setLength(ECHOMAX); // Reset length to avoid shrinking buffer
        }
        /* NOT REACHED */
    }
}