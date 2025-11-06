package academy.generator;

import academy.model.CellType;
import academy.model.Maze;
import academy.util.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BaseGenerator implements Generator {

    protected final Random random;

    protected static final int[][] DIRECTIONS = {{2, 0}, {0, 2}, {-2, 0}, {0, -2}};

    protected BaseGenerator() {
        this.random = new Random();
    }

    protected BaseGenerator(long seed) {
        this.random = new Random(seed);
    }

    public abstract Maze generate(int width, int height);

    protected boolean isValidCell(Maze maze, int x, int y) {
        return x > 0 && x < maze.getFullWidth() - 1 && y > 0 && y < maze.getFullHeight() - 1;
    }

    protected List<int[]> getShuffledDirections() {
        List<int[]> directions = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            directions.add(dir.clone());
        }
        Collections.shuffle(directions, random);
        return directions;
    }

    protected void createPassage(Maze maze, int x1, int y1, int x2, int y2) {
        maze.setCell(x1, y1, CellType.PASSAGE);
        maze.setCell(x2, y2, CellType.PASSAGE);

        int wallX = (x1 + x2) / 2;
        int wallY = (y1 + y2) / 2;
        maze.setCell(wallX, wallY, CellType.PASSAGE);
    }

    protected Maze initializeMaze(int width, int height) {
        Validator.validateMazeSize(width, height);
        return new Maze(width, height);
    }
}
