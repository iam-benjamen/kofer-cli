package kofer;

import kofer.cli.KoferCLI;
import kofer.exception.KoferException;

public class Main {
    public static void main(String[] args) {
        try {
            boolean debugMode = false;
            String[] processArgs = args;

            if (args.length > 0 && ("--debug".equals(args[0]) || "-d".equals(args[0]))) {
                debugMode = true;
                processArgs = new String[args.length - 1];
                System.arraycopy(args, 1, processArgs, 0, args.length - 1);
            }

            KoferCLI cli = new KoferCLI(debugMode);

            if (processArgs.length == 0) {
                cli.showHelp();
                return;
            }

            cli.processCommand(processArgs);

        } catch (KoferException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}