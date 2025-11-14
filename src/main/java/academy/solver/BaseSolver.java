package academy.solver;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseSolver implements Solver {

    private static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public abstract Path solve(Maze maze, Point start, Point end);

    protected List<Point> getPassableNeighbors(Maze maze, Point current) {
        List<Point> neighbors = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            int newX = current.x() + dir[0];
            int newY = current.y() + dir[1];

            if (maze.isPassage(newX, newY)) {
                neighbors.add(new Point(newX, newY));
            }
        }

        return neighbors;
    }

    protected Path reconstructPath(Map<Point, Point> cameFrom, Point end) {
        List<Point> pathPoints = new ArrayList<>();
        // Point current = end;

        while (end != null) {
            pathPoints.add(end);
            end = cameFrom.get(end);
        }

        Collections.reverse(pathPoints);
        return new Path(pathPoints);
    }

    protected int heuristic(Point from, Point to) {
        return from.manhattanDistance(to);
    }

    protected Path emptyPath() {
        return new Path();
    }
}
