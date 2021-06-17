import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ConstructTree {

    public Map<String, Integer> fileFreq = new HashMap<>();
    public Map<String, String> codeTable = new HashMap<>();
    Vector<String> fileData = new Vector<>();
    Vector<Integer> numOfChars = new Vector<>();
    //    File file;
//    Boolean folder;
    int countNewLines = 0;

//    public ConstructTree(String fileName, Boolean folder) {
//        file = new File(fileName);
//        this.folder = folder;
//    }

    public void traverseTree(Node root, String code) {

        if (root.leftChild == null && root.rightChild == null) {
            codeTable.put(root.character, code);
            return;
        }
        traverseTree(root.rightChild, code + "1");
        traverseTree(root.leftChild, code + "0");
    }

    void readFile(File file) {
        try {
            int countCharacters = 0;
            Scanner myReader = new Scanner(file);
            myReader.useDelimiter("\n");
            String test = myReader.next();
            if (test.charAt(0) == '\r') {
                countNewLines++;
                countCharacters++;
                fileData.add("NR");
            }
            myReader = new Scanner(file);
            myReader.useDelimiter("\r\n");
            while (myReader.hasNext()) {
                String data = myReader.next();
                for (int i = 0; i < data.length(); i++) {
                    fileData.add(Character.toString(data.charAt(i)));
                    countCharacters++;
                    if (!fileFreq.containsKey(Character.toString(data.charAt(i))))
                        fileFreq.put(Character.toString(data.charAt(i)), 1);
                    else {
                        int oldValue = fileFreq.get(Character.toString(data.charAt(i)));
                        fileFreq.replace(Character.toString(data.charAt(i)), oldValue + 1);
                    }
                }
                if (myReader.hasNext()) {
                    countNewLines++;
                    fileData.add("NR");
                    countCharacters++;
                }
            }
            myReader.close();
            numOfChars.add(countCharacters);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void makeTree() {

        if (countNewLines > 0)
            fileFreq.put("NR", countNewLines);

        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(fileFreq.size() + 1, new ModifiedComparator());
        for (Map.Entry<String, Integer> e : fileFreq.entrySet()) {
            Node node = new Node();
            node.frequency = e.getValue();
            node.character = e.getKey();
            priorityQueue.add(node);

        }

        while (!priorityQueue.isEmpty()) {
            Node nodeLeft = priorityQueue.remove();
            Node nodeRight = priorityQueue.remove();
            Node parent = new Node();
            parent.leftChild = nodeLeft;
            parent.rightChild = nodeRight;
            parent.frequency = nodeLeft.frequency + nodeRight.frequency;
            priorityQueue.add(parent);
            if (priorityQueue.size() == 1)
                break;
        }
        Node root = priorityQueue.remove();
        traverseTree(root, "");
    }

    void compressFile(String fileName) {
        long startTime = System.nanoTime();
        File file = new File(fileName);
        float originalSize = file.length();
        if (file.exists()) {
            readFile(file);
            makeTree();
            Compress compress = new Compress(fileData, codeTable, file, numOfChars);
            compress.makeFileBinary();
            compress.makeBinaryAscii(false);
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("\nCompression Done");
            System.out.println("Total execution time to compress File: " + elapsedTime / 1000000 + " ms");
            float compressionRatio = (file.length() / originalSize) * 100;
            System.out.println("Compression Ratio: " + compressionRatio + " %");
            System.out.println("Used encoding");
            for (Map.Entry<String, String> e : codeTable.entrySet())
                System.out.println(e.getKey() + ":" + e.getValue());
        } else
            System.out.println("\nFile does not exist");

    }

    void compressFolder(String folderName) {
        long startTime = System.nanoTime();
        File folder = new File(folderName);
        float originalSize = 0;
        Vector<String> fileNames = new Vector<>();
        if (folder.exists()) {
            File[] filesList = folder.listFiles();
            for (int i = 0; i < filesList.length; i++) {
                readFile(filesList[i]);
                fileNames.add(filesList[i].getName());
                originalSize += filesList[i].length();
            }
            makeTree();
            File file = new File(folderName + ".txt");
            Compress compress = new Compress(fileData, codeTable, file, numOfChars, fileNames);
            compress.makeFileBinary();
            compress.makeBinaryAscii(true);
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("\nCompression Done");
            System.out.println("Total execution time to compress Folder: " + elapsedTime / 1000000 + " ms");
            float compressionRatio = (file.length() / originalSize) * 100;
            System.out.println("Compression Ratio: " + compressionRatio + " %");
            System.out.println("Used encoding");
            for (Map.Entry<String, String> e : codeTable.entrySet())
                System.out.println(e.getKey() + ":" + e.getValue());
        } else
            System.out.println("\nFolder does not exist");
    }
}