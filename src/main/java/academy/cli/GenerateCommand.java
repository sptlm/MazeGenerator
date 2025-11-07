package academy.cli;

import academy.generator.BaseGenerator;
import academy.generator.impl.CyclicMazeGenerator;
import academy.generator.impl.DfsGenerator;
import academy.generator.impl.PrimGenerator;
import academy.model.Maze;
import academy.util.FileHandler;
import academy.util.UnicodeRenderer;
import academy.util.Validator;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "generate",
        description = "Generate a maze with specified algorithm and dimensions.",
        mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateCommand.class);

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

    @Option(
            names = {"-u", "--unicode"},
            description = "Use Unicode box-drawing symbols for rendering",
            defaultValue = "false")
    private boolean useUnicode;

    @Override
    public Integer call() {
        LOGGER.atInfo().log("Generative process launched.");
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
            String result;
            if (useUnicode) {
                result = UnicodeRenderer.render(maze);
            } else {
                result = maze.toString();
            }
            if (outputFile != null && !outputFile.trim().isEmpty()) {
                FileHandler.save(result, outputFile);
                System.out.println("Maze saved to: " + outputFile);
            } else {
                System.out.println(result);
            }

            return 0;
        } catch (IllegalArgumentException e) {
            LOGGER.atError().setCause(e).log("Error occurred during generative process.");
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Unexpected error occurred during generative process.");
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
}
