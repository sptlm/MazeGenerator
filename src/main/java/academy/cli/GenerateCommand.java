package academy.cli;

import academy.generator.BaseGenerator;
import academy.generator.impl.CyclicMazeGenerator;
import academy.generator.impl.DfsGenerator;
import academy.generator.impl.PrimGenerator;
import academy.model.Maze;
import academy.util.FileHandler;
import academy.util.Validator;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generate", description = "Generate a maze using specified algorithm", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    @Option(
            names = {"-a", "--algorithm"},
            description = "Generation algorithm: dfs, prim",
            required = true)
    private String algorithm;

    @Option(
            names = {"-w", "--width"},
            description = "Maze width (without borders)",
            required = true)
    private int width;

    @Option(
            names = {"-h", "--height"},
            description = "Maze height (without borders)",
            required = true)
    private int height;

    @Option(
            names = {"-o", "--output"},
            description = "Output file name (if not specified, prints to console)")
    private String outputFile;

    @Option(
            names = {"-c", "--cycle-chance"},
            description = "Probability of adding cycles (0.0-1.0, for cyclic algorithm only)",
            defaultValue = "0.2")
    private float cycleChance;

    @Option(
            names = {"-s", "--surface-variation"},
            description = "Probability of surface variation (0.0-1.0, for cyclic algorithm only)",
            defaultValue = "0.3")
    private float surfaceVariationChance;

    @Override
    public Integer call() {
        try {
            Validator.validateMazeSize(width, height);

            Maze maze =
                    switch (algorithm.toLowerCase()) {
                        case "dfs" -> {
                            BaseGenerator gen = new DfsGenerator();
                            yield gen.generate(width, height);
                        }
                        case "prim" -> {
                            BaseGenerator gen = new PrimGenerator();
                            yield gen.generate(width, height);
                        }
                        case "cyclic" -> {
                            CyclicMazeGenerator gen = new CyclicMazeGenerator(
                                    System.currentTimeMillis(), cycleChance, surfaceVariationChance);
                            yield gen.generate(width, height);
                        }
                        default ->
                            throw new IllegalArgumentException(
                                    "Unknown algorithm: " + algorithm + ". Available: dfs, prim, cyclic");
                    };

            if (outputFile != null && !outputFile.trim().isEmpty()) {

                FileHandler.saveMaze(maze, outputFile);

                System.out.println("Maze saved to: " + outputFile);
            } else {
                System.out.println(maze.toString());
            }

            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
}
