package academy.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Path {
    private final List<Point> points;

    public Path() {
        this.points = new ArrayList<>();
    }

    public Path(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int size() {
        return points.size();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public String applyToMaze(Maze maze, Point start, Point end) {
        Set<Point> pointSet = new HashSet<>(points);
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < maze.getFullHeight(); y++) {
            for (int x = 0; x < maze.getFullWidth(); x++) {
                Point current = new Point(x, y);

                if (current.equals(start)) {
                    sb.append('O');
                } else if (current.equals(end)) {
                    sb.append('X');
                } else if (pointSet.contains(current)) {
                    sb.append('.');
                } else {
                    sb.append(maze.getCell(x, y).getSymbol());
                }
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
