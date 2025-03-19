package UDPSocket;

import java.net.*;  // for DatagramSocket, DatagramPacket, and InetAddress
import java.io.*;   // for IOException
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class myFirstUDPClient {

    private static final int TIMEOUT = 3000;   // Resend timeout (milliseconds)
    private static final int MAXTRIES = 5;     // Maximum retransmissions

    public static void main(String[] args) throws IOException {
        if (args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Server> [<Port>]");

        double[] runTimes = new double[7]; // Array to store round-trip times
        Scanner scanner = new Scanner(System.in);
        String[] runs = {"first", "second", "third", "fourth", "fifth", "sixth", "seventh"};

        // Convert input String to bytes using the default character encoding
        for (int i = 0; i < 7; i++) {
            byte[] bytesToSend = new byte[2];
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

            ByteBuffer.wrap(bytesToSend).order(ByteOrder.BIG_ENDIAN).putShort(val); // convert short to byte array
            System.out.print("\nSending Bytes: ");
            for (byte b : bytesToSend) {
                System.out.printf("0x%02X ", b);
            }
            System.out.println();

            InetAddress serverAddress = InetAddress.getByName(args[0]);  // Server name or IP address
            int servPort = Integer.parseInt(args[1]);

            DatagramSocket socket = new DatagramSocket();

            socket.setSoTimeout(TIMEOUT);  // Maximum receive blocking time (milliseconds)

            DatagramPacket sendPacket = new DatagramPacket(bytesToSend,  // Sending packet
                    bytesToSend.length, serverAddress, servPort);

            String numStr = Short.toString(val);
            int recvMsgSize = numStr.length() * 2 + 2; // Size of received message (UTF-16)

            DatagramPacket receivePacket = new DatagramPacket(new byte[recvMsgSize], recvMsgSize); // Receiving packet

            int tries = 0;      // Packets may be lost, so we have to keep trying
            boolean receivedResponse = false;
            do {
                socket.send(sendPacket);          // Send the echo string
                try {
                    socket.receive(receivePacket);  // Attempt echo reply reception

                    if (!receivePacket.getAddress().equals(serverAddress))  // Check source
                        throw new IOException("Received packet from an unknown source");

                    receivedResponse = true;
                } catch (InterruptedIOException e) {  // We did not get anything
                    tries += 1;
                    System.out.println("Timed out, " + (MAXTRIES - tries) + " more tries...");
                }
            } while ((!receivedResponse) && (tries < MAXTRIES));

            if (receivedResponse) {
                byte[] recvBuffer = receivePacket.getData();

                System.out.print("Received Bytes: ");
                for (byte r : recvBuffer) {
                    System.out.printf("0x%02X ", r);
                }
                System.out.println("\n");
            } else {
                System.out.println("No response -- giving up.");
            }

            socket.close();
        }
        scanner.close();

    }
}