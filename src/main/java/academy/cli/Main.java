package academy.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "maze-app",
        description = "Maze generator and solver CLI application.",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {GenerateCommand.class, SolveCommand.class})
public class Main implements Runnable {
    public static void main(String[] args) {
        System.setProperty("line.separator", "\n");

        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }
}
