package client;

import java.io.*;
import java.util.Scanner;

public class Main {
    private static final String DIR = "src/client/data";

    private static String queryBuilder (String method, Scanner in) {
        StringBuilder queryBld = new StringBuilder(method + " ");
        System.out.print("Do you want to " + method + " the file by name or by id (1 - name, 2 - id): ");

        int by = in.nextInt();

        if (by == 1) {
            queryBld.append("by_name ");
            System.out.print("Enter name: ");
        } else if (by == 2) {
            queryBld.append("by_id ");
            System.out.print("Enter id: ");
        }

        String id = in.next();

        return  queryBld.append(id).toString();
    }

    private static void menu(DataInputStream inputStream, DataOutputStream outputStream) throws IOException {

        Scanner in = new Scanner(System.in);
        String cmd;

        System.out.print("Enter action (1 - get the file, 2 - create a file, 3 - delete the file): ");
        cmd = in.nextLine();
        if (cmd.matches("[1-3]")) {
            int request = Integer.parseInt(cmd);

            if (request == 1) {
                outputStream.writeUTF(queryBuilder("get", in));
            } else if (request == 2) {

                System.out.print("Enter name of the file: ");
                String fileName = in.nextLine();
                System.out.print("Enter name of the file to be saved on server: ");
                String serverFileName = in.nextLine();
                outputStream.writeUTF("add " + fileName + " " + serverFileName);

            } else if (request == 3) {
                outputStream.writeUTF(queryBuilder("delete", in));
            }

            System.out.println("The request was sent.");
            String serverResponse = inputStream.readUTF();

            switch (serverResponse) {
                case "200":
                    if (request == 1) {
                        int length = inputStream.readInt();
                        byte[] fetchedBytes = new byte[length];
                        inputStream.readFully(fetchedBytes, 0, length);
                        in.nextLine();
                        System.out.print("The file was downloaded! Specify a name for it: ");
                        String fileName = in.nextLine();

                        try (FileOutputStream fileStream = new FileOutputStream(new File(DIR, fileName))) {
                            fileStream.write(fetchedBytes);
                        } finally {
                            System.out.println("File saved on the hard drive!");
                        }
                    } else if (request == 2) {
                        System.out.println("Response says that file is saved! ID = " + inputStream.readInt());
                    } else if (request == 3) {
                        System.out.print("The response says that the file was deleted successfully!");
                    }
                    break;
                case "403":
                    System.out.println("The response says that creating the file was forbidden!");
                    break;
                case "404":
                    System.out.println("The response says that this file is not found!");
                    break;
            }
        } else {
            outputStream.writeUTF("exit");
        }
    }

    public static void main (String[] args) throws IOException, InterruptedException {
        Connection serverCon = new Connection();

        DataInputStream inputStream = serverCon.getInputStream();
        DataOutputStream outputStream = serverCon.getOutputStream();

        menu(inputStream, outputStream);

        serverCon.shutdown();
    }
}
