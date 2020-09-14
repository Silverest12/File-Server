package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Connection {
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final Socket socket;

    Connection () throws IOException, InterruptedException {
        String address = "127.0.0.1";
        int port = 6666;
        socket = new Socket(InetAddress.getByName(address), port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        checkForConnection();
    }

    private void checkForConnection() throws InterruptedException, IOException {
        outputStream.writeUTF("Client started!");

        while (true) {
            String str = inputStream.readUTF();
            if(str.equals("Server started!"))
                break;
            Thread.sleep(50);
        }
    }

    Connection(String address, int port) throws IOException {
        socket = new Socket(InetAddress.getByName(address), port);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void shutdown() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
