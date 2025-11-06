package academy.generator.impl;

import academy.generator.BaseGenerator;
import academy.model.CellType;
import academy.model.Maze;
import java.util.ArrayList;
import java.util.List;

/**
 * Генератор лабиринтов методом алгоритма Прима. Создает идеальный лабиринт путем постепенного добавления ячеек к
 * растущему дереву.
 *
 * <p>Принцип работы: 1. Начинаем со случайной ячейки и добавляем ее в лабиринт 2. Добавляем все соседние стены этой
 * ячейки в список границ 3. Пока список границ не пуст: - Выбираем случайную стену из списка - Если ячейка за стеной
 * еще не в лабиринте: - Делаем эту стену проходом - Добавляем ячейку за стеной в лабиринт - Добавляем соседние стены
 * новой ячейки в список границ - Удаляем обработанную стену из списка
 */
public class PrimGenerator extends BaseGenerator {
    private boolean[][] inMaze; // Массив ячеек, добавленных в лабиринт

    public PrimGenerator() {
        super();
    }

    public PrimGenerator(long seed) {
        super(seed);
    }

    @Override
    public Maze generate(int width, int height) {
        Maze maze = initializeMaze(width, height);
        inMaze = new boolean[height + 2][width + 2];

        List<Wall> walls = new ArrayList<>();

        int startX = 1;
        int startY = 1;

        addCellToMaze(maze, startX, startY, walls);

        while (!walls.isEmpty()) {
            int wallIndex = random.nextInt(walls.size());
            Wall wall = walls.remove(wallIndex);

            if (!inMaze[wall.cellY][wall.cellX]) {
                maze.setCell(wall.wallX, wall.wallY, CellType.PASSAGE);
                maze.setCell(wall.cellX, wall.cellY, CellType.PASSAGE);
                inMaze[wall.cellY][wall.cellX] = true;

                addNeighborWalls(maze, wall.cellX, wall.cellY, walls);
            }
        }

        return maze;
    }

    private void addCellToMaze(Maze maze, int x, int y, List<Wall> walls) {
        maze.setCell(x, y, CellType.PASSAGE);
        inMaze[y][x] = true;

        addNeighborWalls(maze, x, y, walls);
    }

    private void addNeighborWalls(Maze maze, int x, int y, List<Wall> walls) {
        for (int[] dir : directions()) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Проверяем, что соседняя ячейка корректна и еще не в лабиринте
            if (isValidCell(maze, newX, newY) && !inMaze[newY][newX]) {
                int wallX = x + dir[0] / 2;
                int wallY = y + dir[1] / 2;
                walls.add(new Wall(wallX, wallY, newX, newY));
            }
        }
    }

    private static class Wall {
        final int wallX;
        final int wallY;
        final int cellX;
        final int cellY;

        Wall(int wallX, int wallY, int cellX, int cellY) {
            this.wallX = wallX;
            this.wallY = wallY;
            this.cellX = cellX;
            this.cellY = cellY;
        }
    }
}
