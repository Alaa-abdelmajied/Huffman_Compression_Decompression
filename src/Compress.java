import java.io.*;
import java.util.Map;
import java.util.Vector;

public class Compress {

    Vector<String> fileData;
    Vector<Integer> numOfChars;
    Map<String, String> codeTable;
    Vector<String> fileNames;
    StringBuilder readFileBinary = new StringBuilder();
    File file;

    public Compress(Vector<String> fileData, Map<String, String> codeTable, File file, Vector<Integer> numOfChars) {

        this.fileData = fileData;
        this.codeTable = codeTable;
        this.file = file;
        this.numOfChars = numOfChars;
    }

    public Compress(Vector<String> fileData, Map<String, String> codeTable, File file, Vector<Integer> numOfChars, Vector<String> fileNames) {

        this.fileData = fileData;
        this.codeTable = codeTable;
        this.file = file;
        this.numOfChars = numOfChars;
        this.fileNames = fileNames;
    }

    public void makeFileBinary() {

        for (int i = 0; i < fileData.size(); i++) {
            if (!fileData.get(i).equals("NR"))
                readFileBinary.append(codeTable.get(fileData.get(i)));
            else
                readFileBinary.append(codeTable.get("NR"));
        }
    }

    public void makeBinaryAscii(boolean isFolder) {
        char ascii;
        int numOfZeros = 8 - (readFileBinary.length() % 8);
        for (int i = 0; i < numOfZeros; i++)
            readFileBinary.append("0");
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(numOfChars.size() + "\n");
            if (isFolder) {
                for (int i = 0; i < numOfChars.size(); i++) {
                    fileWriter.write(fileNames.get(i) + "\n");
                    fileWriter.write(numOfChars.get(i) + "\n");
                }
            } else {
                for (int i = 0; i < numOfChars.size(); i++)
                    fileWriter.write(numOfChars.get(i) + "\n");
            }
            fileWriter.write(codeTable.size() + "\n" + numOfZeros + "\n");
            for (Map.Entry<String, String> e : codeTable.entrySet()) {
                if (!e.getKey().equals("\n"))
                    fileWriter.write(e.getKey() + ":" + e.getValue() + "\n");
                else
                    fileWriter.write("NL:" + e.getValue() + "\n");

            }
            fileWriter.close();
            Writer outputStream = new OutputStreamWriter(new FileOutputStream(file, true), "ISO_8859_1");
            for (int i = 0; i < readFileBinary.length() / 8; i++) {

                ascii = (char) Integer.parseInt(readFileBinary.substring(8 * i, (i + 1) * 8), 2);
                outputStream.write(ascii);
            }
            outputStream.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
