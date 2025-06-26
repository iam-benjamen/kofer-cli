package kofer;

import kofer.cli.KoferCLI;
import kofer.exception.KoferException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try{
            KoferCLI cli = new KoferCLI();
            cli.start();
            scanner.close();
        } catch (KoferException e){
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e){
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}