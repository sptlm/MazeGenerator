package academy.cli;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import academy.solver.BaseSolver;
import academy.solver.impl.AStarSolver;
import academy.solver.impl.BiDirectionalWeightedSolver;
import academy.solver.impl.DijkstraSolver;
import academy.util.FileHandler;
import academy.util.UnicodeRenderer;
import academy.util.Validator;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = "solve",
        description = "Solve a maze with specified algorithm and points.",
        mixinStandardHelpOptions = true)
public class SolveCommand implements Callable<Integer> {
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
    public Integer call() {
        LOGGER.atInfo().log("Solving process launched.");
        try {
            Maze maze = FileHandler.loadMaze(inputFile);

            Point start = Validator.parseCoordinates(startCoords);
            Point end = Validator.parseCoordinates(endCoords);

            BaseSolver solver =
                    switch (algorithm.toLowerCase()) {
                        case "astar" -> new AStarSolver();
                        case "dijkstra" -> new DijkstraSolver();
                        case "bidirectional" -> new BiDirectionalWeightedSolver();
                        default ->
                            throw new IllegalArgumentException(
                                    "Unknown algorithm: " + algorithm + ". Available: astar, dijkstra, bidirectional");
                    };

            Path path = solver.solve(maze, start, end);

            if (path.isEmpty()) {
                System.err.println("No path found from " + start + " to " + end);
                return 1;
            }
            String result;
            if (useUnicode) {
                result = UnicodeRenderer.renderWithPath(maze, path, start, end);
            } else {
                result = path.renderMaze(maze, start, end);
            }

            if (outputFile != null && !outputFile.trim().isEmpty()) {
                FileHandler.save(result, outputFile);
                System.out.println("Solution saved to: " + outputFile);
            } else {
                System.out.println(result);
            }

            return 0;
        } catch (IllegalArgumentException e) {
            LOGGER.atError().setCause(e).log("Error occurred during solving process.");
            System.err.println(e.getMessage());
            return 1;
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Unexpected error occurred during solving process.");
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
}
