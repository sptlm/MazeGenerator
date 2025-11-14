package academy.cli;

import academy.generator.Generator;
import academy.model.Maze;
import academy.util.FileHandler;
import academy.util.UnicodeRenderer;
import academy.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "generate",
        description = "Generate a maze with specified algorithm and dimensions.",
        mixinStandardHelpOptions = true)
public class GenerateCommand implements Runnable {
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
    public void run() {
        LOGGER.atInfo().log("Generative process launched.");
        try {
            Validator.validateMazeSize(width, height);

            Generator generator = null;
            for (GeneratorNames gen : GeneratorNames.values()) {
                if (gen.getName().equals(algorithm)) {
                    generator = gen.getGenerator();
                    break;
                }
            }
            if (generator == null) {
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }
            Maze maze = generator.generate(width, height);
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
        } catch (IllegalArgumentException e) {
            LOGGER.atError().setCause(e).log("Error occurred during generative process.");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Unexpected error occurred during generative process.");
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
