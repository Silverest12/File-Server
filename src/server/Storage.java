package server;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class Storage implements Serializable {
    private static final long serialVersionUID = 666L;

    private final HashMap<Integer, String> filesList;
    private final File DIR;
    private int lastId;

    Storage(String directory) {
        DIR = new File(directory);

        filesList = new HashMap<>();
        lastId = 10;
    }

    public int getLastId() {
        return lastId;
    }

    public byte[] getFile(String fileName) {
        if (filesList.containsValue(fileName)) {
            for (int k : filesList.keySet()) {
                if (filesList.get(k).equals(fileName)) {
                    File fetchedFile = new File(DIR, fileName);
                    return readFileToByteArray(fetchedFile);
                }
            }
        }
        return null;
    }

    public byte[] getFile(int id) {
        if (filesList.containsKey(id)) {
            File fetchedFile = new File(DIR, filesList.get(id));
            return readFileToByteArray(fetchedFile);
        }
        return null;
    }

    private byte[] readFileToByteArray(File file) {
        FileInputStream fis;

        byte[] byteArr = new byte[(int) file.length()];

        try {
            fis = new FileInputStream(file);
            fis.read(byteArr);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteArr;
    }

    private String readFile(File file) throws IOException {
        StringBuilder fileContent = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));

        int c;

        while ((c = br.read()) != -1) {
            fileContent.append((char) c);
        }
        return fileContent.toString();
    }

    public boolean addFile(String fileName, String providedName) throws IOException {
        File newFile = new File(DIR, providedName);
        filesList.put(lastId++, newFile.getName());
        return newFile.createNewFile() && writeToFile(providedName, "test" + filesList.size());
    }

    public boolean writeToFile(String fileName, String fileContent) throws IOException {

        File fileToMod = new File(DIR, fileName);

        if (fileToMod.exists()) {
            FileWriter fw = new FileWriter(fileToMod);
            fw.write(fileContent);
            fw.close();
            return true;
        }
        return false;
    }

    public boolean deleteFile(int id) {
        File fileToDel = new File(DIR, filesList.get(id));
        filesList.remove(id);
        return fileToDel.delete();
    }

    public boolean deleteFile(String fileName) {
        for (int id : filesList.keySet()) {
            if (filesList.get(id).equals(fileName)) {
                File fileToDel = new File(DIR, fileName);
                filesList.remove(id);
                if (!fileToDel.exists() || !fileToDel.delete())
                    return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Storage)) return false;
        Storage storage = (Storage) o;
        return filesList.equals(storage.filesList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filesList);
    }

    static void saveFileMap(Storage st) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\silverest\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\save\\fileMap.txt");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(st);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
    }

    static Storage loadFileMap() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\silverest\\IdeaProjects\\File Server\\File Server\\task\\src\\server\\save\\fileMap.txt");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Storage tmp = (Storage) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return tmp;
    }
}
