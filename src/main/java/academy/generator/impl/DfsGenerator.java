package academy.generator.impl;

import academy.generator.BaseGenerator;
import academy.model.CellType;
import academy.model.Maze;
import java.util.List;

/**
 * Генератор лабиринтов методом поиска в глубину (Depth-First Search).
 * Алгоритм создает идеальный лабиринт (без циклов) с единственным путем между любыми двумя точками.
 *
 * Принцип работы:
 * 1. Начинаем со случайной ячейки
 * 2. Помечаем текущую ячейку как посещенную
 * 3. Пока есть непосещенные соседи:
 *    - Выбираем случайного непосещенного соседа
 *    - Удаляем стену между текущей ячейкой и соседом
 *    - Рекурсивно повторяем для соседа
 * 4. Когда зашли в тупик, возвращаемся назад
 */
public class DfsGenerator extends BaseGenerator {
    private boolean[][] visited;

    public DfsGenerator() {
        super();
    }

    public DfsGenerator(long seed) {
        super(seed);
    }

    @Override
    public Maze generate(int width, int height) {
        Maze maze = initializeMaze(width, height);
        visited = new boolean[height + 2][width + 2];

        dfs(maze, 1, 1);
        return maze;
    }

    private void dfs(Maze maze, int x, int y) {
        maze.setCell(x, y, CellType.PASSAGE);
        visited[y][x] = true;

        List<int[]> directions = getShuffledDirections();

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidCell(maze, newX, newY) && !visited[newY][newX]) {
                createPassage(maze, x, y, newX, newY);
                dfs(maze, newX, newY);
            }
        }
    }
}
