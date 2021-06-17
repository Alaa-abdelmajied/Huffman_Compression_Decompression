import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Decompress {

    Map<String, String> decompressCodeTable = new HashMap<>();
    StringBuilder readFileBinary = new StringBuilder();
    Vector<Integer> numOfChars = new Vector<>();
    Vector<String> fileNames = new Vector<>();
    BufferedReader bufferedReader;
    int padding;
    int pointerBinary = 0;

    void readFileHeader(File file, boolean isFolder) {
        int tableSize;
        int numOfFiles;
        String line;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "8859_1"));
            numOfFiles = Integer.parseInt(bufferedReader.readLine());
            if (isFolder) {
                for (int i = 0; i < numOfFiles; i++) {
                    fileNames.add(bufferedReader.readLine());
                    numOfChars.add(Integer.parseInt(bufferedReader.readLine()));
                }
            } else {
                for (int i = 0; i < numOfFiles; i++)
                    numOfChars.add(Integer.parseInt(bufferedReader.readLine()));
            }
            tableSize = Integer.parseInt(bufferedReader.readLine());
            padding = Integer.parseInt(bufferedReader.readLine());
            for (int i = 0; i < tableSize; i++) {
                line = bufferedReader.readLine();
                if (line.charAt(0) != ':') {
                    String[] lineSplit = line.split(":");
                    if (lineSplit[0].equals("NL"))
                        decompressCodeTable.put(lineSplit[1], "\n");
                    else
                        decompressCodeTable.put(lineSplit[1], lineSplit[0]);
                } else {
                    decompressCodeTable.put(line.substring(2), Character.toString(line.charAt(0)));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readFileEncoded() {
        try {
            String codedLine = "";
            int i = 0;
            boolean flag = false;
            while (i >= 0) {
                if (flag) {
                    codedLine = "";
                    flag = false;
                }
                i = bufferedReader.read();
                if ((char) i == '\n') {
                    flag = true;
                    AsciiToBinary(codedLine, 1);
                } else if ((char) i == '\r') {
                    flag = true;
                    AsciiToBinary(codedLine, 2);
                }
                codedLine += (char) i;
            }
            if (!codedLine.isEmpty()) {
                AsciiToBinary(codedLine, 0);
            }
            removePadding(padding);
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void AsciiToBinary(String asciiString, int appendWhat) {
        byte[] bytes = asciiString.getBytes();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                readFileBinary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        if (appendWhat == 1)
            readFileBinary.append("00001010");
        else if (appendWhat == 2)
            readFileBinary.append("00001101");
    }

    void removePadding(int padding) {
        readFileBinary = readFileBinary.delete(readFileBinary.length() - (padding + 8), readFileBinary.length());
    }

    void binaryToString(File file, int fileNum) {
        StringBuilder code = new StringBuilder();
        int charCounter = 0;
        Vector<String> realData = new Vector<>();
        for (int i = pointerBinary; i < readFileBinary.length() && charCounter != numOfChars.get(fileNum); i++) {
            code.append(readFileBinary.charAt(i));
            pointerBinary++;
            if (decompressCodeTable.containsKey(code.toString())) {
                realData.add(decompressCodeTable.get(code.toString()));
                code.setLength(0);
                charCounter++;
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(file, false);
            String word;
            for (int i = 0; i < realData.size(); i++) {
                word = realData.get(i);
                if (word.equals("NR"))
                    fileWriter.write("\r\n");
                else
                    fileWriter.write(word);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void decompressFile(String fileName) {
        long startTime = System.nanoTime();
        File file = new File(fileName);
        if (file.exists()) {
            readFileHeader(file, false);
            readFileEncoded();
            binaryToString(file, 0);
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("\nDecompression Done");
            System.out.println("Total execution time to compress File: " + elapsedTime / 1000000 + " ms");
            System.out.println("Used encoding");
            for (Map.Entry<String, String> e : decompressCodeTable.entrySet())
                System.out.println(e.getKey() + ":" + e.getValue());
        } else
            System.out.println("\nFile does not exist");
    }

    void decompressFolder(String fileName) {
        long startTime = System.nanoTime();
        File file = new File(fileName);
        if (file.exists()) {
            readFileHeader(file, true);
            readFileEncoded();
            String folderName = fileName.substring(0, fileName.length() - 4);
            File folder = new File(folderName);
            folder.mkdir();
            for (int i = 0; i < numOfChars.size(); i++)
                binaryToString(new File(folderName + "\\" + fileNames.get(i)), i);
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("\nDecompression Done");
            System.out.println("Total execution time to compress Folder: " + elapsedTime / 1000000 + " ms");
            System.out.println("Used encoding");
            for (Map.Entry<String, String> e : decompressCodeTable.entrySet())
                System.out.println(e.getKey() + ":" + e.getValue());
        } else
            System.out.println("\nFile does not exist");
    }
}
