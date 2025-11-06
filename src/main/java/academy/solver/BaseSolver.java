package academy.solver;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseSolver {

    protected static final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public abstract Path solve(Maze maze, Point start, Point end);

    protected void validatePoints(Maze maze, Point start, Point end) {
        if (!maze.isValidCoordinate(start.x(), start.y())) {
            throw new IllegalArgumentException("Start point is out of bounds: " + start);
        }
        if (!maze.isValidCoordinate(end.x(), end.y())) {
            throw new IllegalArgumentException("End point is out of bounds: " + end);
        }
        if (!maze.isPassage(start.x(), start.y())) {
            throw new IllegalArgumentException("Start point is not a passage: " + start);
        }
        if (!maze.isPassage(end.x(), end.y())) {
            throw new IllegalArgumentException("End point is not a passage: " + end);
        }
    }

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
        Point current = end;

        while (current != null) {
            pathPoints.add(current);
            current = cameFrom.get(current);
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
