package academy.cli;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import academy.solver.Solver;
import academy.util.FileHandler;
import academy.util.UnicodeRenderer;
import academy.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "solve",
        description = "Solve a maze with specified algorithm and points.",
        mixinStandardHelpOptions = true)
public class SolveCommand implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolveCommand.class);

    @Option(
            names = {"-a", "--algorithm"},
            description = "Solving algorithm: astar, dijkstra",
            required = true)
    private String algorithm;

    @Option(
            names = {"-f", "--file"},
            description = "Input maze file",
            required = true)
    private String inputFile;

    @Option(
            names = {"-s", "--start"},
            description = "Start coordinates in format x,y",
            required = true)
    private String startCoords;

    @Option(
            names = {"-e", "--end"},
            description = "End coordinates in format x,y",
            required = true)
    private String endCoords;

    @Option(
            names = {"-o", "--output"},
            description = "Output file name (if not specified, prints to console)")
    private String outputFile;

    @Option(
            names = {"-u", "--unicode"},
            description = "Use Unicode box-drawing symbols for rendering",
            defaultValue = "false")
    private boolean useUnicode;

    @Override
    public void run() {
        LOGGER.atInfo().log("Solving process launched.");
        try {
            Maze maze = FileHandler.loadMaze(inputFile);

            Point start = Validator.parseCoordinates(startCoords);
            Point end = Validator.parseCoordinates(endCoords);

            Solver solver = null;
            for (SolverNames sol : SolverNames.values()) {
                if (sol.getName().equals(algorithm)) {
                    solver = sol.getSolver();
                    break;
                }
            }
            if (solver == null) {
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
            }

            Path path = solver.solve(maze, start, end);

            if (path.isEmpty()) {
                System.err.println("No path found from " + start + " to " + end);
            }
            String result;
            if (useUnicode) {
                result = UnicodeRenderer.renderWithPath(maze, path, start, end);
            } else {
                result = path.applyToMaze(maze, start, end);
            }

            if (outputFile != null && !outputFile.trim().isEmpty()) {
                FileHandler.save(result, outputFile);
                System.out.println("Solution saved to: " + outputFile);
            } else {
                System.out.println(result);
            }

        } catch (IllegalArgumentException e) {
            LOGGER.atError().setCause(e).log("Error occurred during solving process.");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Unexpected error occurred during solving process.");
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
