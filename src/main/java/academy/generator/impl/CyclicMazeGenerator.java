package academy.generator.impl;

import academy.generator.BaseGenerator;
import academy.model.CellType;
import academy.model.Maze;
import academy.util.Validator;

/**
 * Генератор не-идеальных лабиринтов с циклами и несколькими путями. Использует dfs генератор для получения идеального
 * лабиринта, а затем добавляет новые проходы.
 */
public class CyclicMazeGenerator extends BaseGenerator {
    private float cycleChance; // Вероятность добавления цикла
    private float surfaceVariationChance; // Вероятность использования других поверхностей
    private DfsGenerator dfsGenerator;

    public CyclicMazeGenerator() {
        super();
        this.cycleChance = 0.2f;
        this.surfaceVariationChance = 0.3f;
        this.dfsGenerator = new DfsGenerator();
    }

    public CyclicMazeGenerator(long seed) {
        super(seed);
        this.cycleChance = 0.2f;
        this.surfaceVariationChance = 0.3f;
        this.dfsGenerator = new DfsGenerator(seed);
    }

    public CyclicMazeGenerator(long seed, float cycleChance, float surfaceVariationChance) {
        super(seed);
        this.cycleChance = Math.max(0.0f, Math.min(1.0f, cycleChance));
        this.surfaceVariationChance = Math.max(0.0f, Math.min(1.0f, surfaceVariationChance));
        this.dfsGenerator = new DfsGenerator(seed);
    }

    public Maze generate(int width, int height) {
        Validator.validateMazeSize(width, height);

        Maze maze = dfsGenerator.generate(width, height);
        // Добавляем циклы для создания множественных путей
        addCycles(maze, width, height);

        // Добавляем различные типы поверхностей
        addSurfaceVariation(maze, width, height);

        return maze;
    }

    private void addCycles(Maze maze, int width, int height) {
        // Количество циклов зависит от размера лабиринта и cycleChance
        int cycleCount = (int) ((width + height) * cycleChance);
        while (cycleCount > 0) {
            int x = random.nextInt(1, width + 1);
            int y = random.nextInt(1, height + 1);
            if (maze.isPassage(x, y)) continue;
            maze.setCell(x, y, CellType.PASSAGE);
            cycleCount--;
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
