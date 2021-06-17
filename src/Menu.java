import java.util.Scanner;

public class Menu {

    void menuDisplay() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("******************Main Menu**********************");
        System.out.println("-> 1- Compress\n-> 2- Decompress");
        System.out.println("*************************************************");
        System.out.print("Select Function: ");
        String input = scanner.nextLine();
        switch (input) {
            case "1":
                System.out.println("******************Compression**********************");
                System.out.println("-> 1- File\n-> 2- Folder");
                System.out.println("*************************************************");
                System.out.print("Select Function: ");
                input = scanner.nextLine();
                ConstructTree constructTree = new ConstructTree();
                switch (input) {
                    case "1":
                        System.out.print("Enter the file name: ");
                        input = scanner.nextLine();
                        constructTree.compressFile(input);
                        returnToMenu();
                        break;

                    case "2":
                        System.out.print("Enter the Folder name: ");
                        input = scanner.nextLine();
                        constructTree.compressFolder(input);
                        returnToMenu();
                        break;

                    default:
                        System.err.println("Invalid Input");
                        menuDisplay();
                        break;
                }
                break;

            case "2":
                System.out.println("******************Decompression**********************");
                System.out.println("-> 1- File\n-> 2- Folder");
                System.out.println("*************************************************");
                System.out.print("Select Function: ");
                input = scanner.nextLine();
                Decompress decompress = new Decompress();
                switch (input) {
                    case "1":
                        System.out.print("Enter the file name: ");
                        input = scanner.nextLine();
                        decompress.decompressFile(input);
                        returnToMenu();
                        break;

                    case "2":
                        System.out.print("Enter the file name: ");
                        input = scanner.nextLine();
                        decompress.decompressFolder(input);
                        returnToMenu();
                        break;

                    default:
                        System.err.println("Invalid Input");
                        menuDisplay();
                        break;

                }
                break;

            default:
                System.err.println("Invalid Input");
                menuDisplay();
                break;
        }
    }

    void returnToMenu() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nPress Y to continue, anything else to exit: ");
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            menuDisplay();

        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.menuDisplay();
    }
}
