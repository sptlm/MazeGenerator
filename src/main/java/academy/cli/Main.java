package academy.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "maze-app",
        description = "Maze generator and solver CLI application.",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {GenerateCommand.class, SolveCommand.class})
public class Main implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.setProperty("line.separator", "\n");

        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        LOGGER.atInfo().log("Program was launched.");
        try {
            CommandLine.usage(this, System.out);
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Error occurred during program work.");
        }
        LOGGER.atInfo().log("Program finished.");
    }
}
