package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class Main {

    private static boolean menu(Storage storage, DataInputStream inputStream, DataOutputStream outputStream) throws IOException {
        String input;

        input = inputStream.readUTF();
        String[] inSplit = input.split("\\s+");
        String command = inSplit[0];
        String rest = null;

        if (inSplit.length >= 2)
            rest = inSplit[1];

        switch (command.toLowerCase()) {
            case "get":
                byte[] fetchedFile = null;
                assert rest != null;

                if (rest.toLowerCase().equals("by_id")) {
                    fetchedFile = storage.getFile(Integer.parseInt(inSplit[2]));
                } else if (rest.toLowerCase().equals("by_name")) {
                    fetchedFile = storage.getFile(inSplit[2]);
                }

                if (fetchedFile != null) {
                    outputStream.writeUTF("200");
                    outputStream.writeInt(fetchedFile.length);
                    outputStream.write(fetchedFile);
                } else {
                    outputStream.writeUTF("404");
                }

                return true;

            case "add":
                assert rest != null;

                String providedName;

                if (inSplit.length >= 3) {
                    providedName = inSplit[2];
                } else {
                    providedName = StringUtils.generateRandomName() + "." + rest.split("\\.")[1];
                }

                if (storage.addFile(rest, providedName)) {
                    outputStream.writeUTF("200");
                    outputStream.writeInt(storage.getLastId() - 1);
                } else {
                    outputStream.writeUTF("403");
                }

                return true;

            case "delete":
                boolean queryChecker = false;
                assert rest != null;

                if (rest.toLowerCase().equals("by_id")) {
                    queryChecker = storage.deleteFile(Integer.parseInt(inSplit[2]));
                } else if (rest.toLowerCase().equals("by_name")) {
                    queryChecker = storage.deleteFile(inSplit[2]);
                }

                if (queryChecker) {
                    outputStream.writeUTF("200");
                } else {
                    outputStream.writeUTF("404");
                }

                return true;
        }

        return false;
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Storage storage;
        if(!new File("src/server/save/fileMap.txt").exists()) {
            storage = new Storage("src/server/data");
        } else {
            storage = Storage.loadFileMap();
        }

        while (true) {
            Connection clientCon = new Connection();

            DataInputStream inputStream = clientCon.getInputStream();
            DataOutputStream outputStream = clientCon.getOutputStream();

            if(!menu(storage, inputStream, outputStream)) {
                Storage.saveFileMap(storage);
                clientCon.shutdown();
                break;
            } else {
                Storage.saveFileMap(storage);
                clientCon.shutdown();
            }
        }

    }
}
