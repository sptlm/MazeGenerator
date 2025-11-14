package academy.generator.impl;

import academy.generator.BaseGenerator;
import academy.generator.Generator;
import academy.model.CellType;
import academy.model.Maze;
import academy.model.Point;
import academy.util.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Генератор не-идеальных лабиринтов с циклами и несколькими путями. Использует dfs генератор для получения идеального
 * лабиринта, а затем добавляет новые проходы.
 */
public class CyclicMazeGenerator extends BaseGenerator {
    private float cycleChance; // Вероятность добавления цикла
    private float surfaceVariationChance; // Вероятность использования других поверхностей
    private Generator perfectGenerator;

    public CyclicMazeGenerator(Generator perfectGenerator) {
        super();
        this.cycleChance = 0.2f;
        this.surfaceVariationChance = 0.3f;
        this.perfectGenerator = perfectGenerator;
    }

    public CyclicMazeGenerator(long seed, Generator perfectGenerator) {
        super(seed);
        this.cycleChance = 0.2f;
        this.surfaceVariationChance = 0.3f;
        this.perfectGenerator = perfectGenerator;
    }

    public CyclicMazeGenerator(long seed, float cycleChance, float surfaceVariationChance, Generator perfectGenerator) {
        super(seed);
        this.cycleChance = Math.max(0.0f, Math.min(1.0f, cycleChance));
        this.surfaceVariationChance = Math.max(0.0f, Math.min(1.0f, surfaceVariationChance));
        this.perfectGenerator = perfectGenerator;
    }

    public Maze generate(int width, int height) {
        Validator.validateMazeSize(width, height);

        Maze maze = perfectGenerator.generate(width, height);
        // Добавляем циклы для создания множественных путей
        addCycles(maze, width, height);

        // Добавляем различные типы поверхностей
        addSurfaceVariation(maze, width, height);

        return maze;
    }

    private void addCycles(Maze maze, int width, int height) {
        List<Point> wallCells = new ArrayList<>();

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                if (!maze.isPassage(x, y)) {
                    wallCells.add(new Point(x, y));
                }
            }
        }
        int cycleCount = (int) ((width + height) * cycleChance);
        cycleCount = Math.min(cycleCount, wallCells.size());

        Collections.shuffle(wallCells, random);

        for (int i = 0; i < cycleCount; i++) {
            Point p = wallCells.get(i);
            maze.setCell(p.x(), p.y(), CellType.PASSAGE);
        }
    }

    private void addSurfaceVariation(Maze maze, int width, int height) {
        CellType[] surfaces = {CellType.ASPHALT, CellType.SAND, CellType.SWAMP};

        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                if (maze.isPassage(x, y) && random.nextFloat() < surfaceVariationChance) {
                    CellType surface = surfaces[random.nextInt(surfaces.length)];
                    maze.setCell(x, y, surface);
                }
            }
        }
    }
}
