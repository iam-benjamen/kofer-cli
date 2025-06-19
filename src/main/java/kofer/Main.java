package kofer;

import kofer.cli.KoferCLI;
import kofer.store.DataStore;

import java.io.File;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        String FILE_PATH = DataStore.APP_DATA_FILE;

        Scanner scanner = new Scanner(System.in);

        DataStore dataStore;
        String password;

        try{
            File dataFile = new File(FILE_PATH);

            if(dataFile.exists()){
                System.out.print("Enter your encryption password: ");
                password = scanner.nextLine();
                dataStore = DataStore.loadData(password);
            } else {
                System.out.print("Create your encryption password: ");
                password = scanner.nextLine();

                dataStore = new DataStore(password);
                dataStore.saveData(password);

                System.out.println("\nWelcome to Kofer! Your data has been saved securely.");
            }

            KoferCLI cli = new KoferCLI(dataStore);
            cli.start();
            scanner.close();
        } catch (Exception e) {
            System.err.println("🚨 Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}