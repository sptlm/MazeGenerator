package academy.solver;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;

/** Решатель лабиринта */
public interface Solver {

    /**
     * Решение лабиринта. Если путь не найден, то возвращается путь с длиной 0.
     *
     * @param maze лабиринт.
     * @param start начальная точка.
     * @param end конечная точка.
     * @return путь в лабиринте.
     */
    Path solve(Maze maze, Point start, Point end);
}
