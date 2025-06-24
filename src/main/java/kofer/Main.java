package kofer;

import kofer.cli.KoferCLI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try{
            KoferCLI cli = new KoferCLI();
            cli.start();
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}