package academy.model;

public class Maze {
    private final int width;
    private final int height;
    private final CellType[][] grid;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[height + 2][width + 2];

        for (int y = 0; y < height + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                grid[y][x] = CellType.WALL;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFullWidth() {
        return width + 2;
    }

    public int getFullHeight() {
        return height + 2;
    }

    public CellType getCell(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        return grid[y][x];
    }

    public void setCell(int x, int y, CellType type) {
        if (!isValidCoordinate(x, y)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        grid[y][x] = type;
    }

    public boolean isPassage(int x, int y) {
        return isValidCoordinate(x, y) && grid[y][x] != CellType.WALL;
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width + 2 && y >= 0 && y < height + 2;
    }

    public int getWeight(int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return Integer.MAX_VALUE;
        }
        return grid[y][x].getWeight();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height + 2; y++) {
            for (int x = 0; x < width + 2; x++) {
                sb.append(grid[y][x].getSymbol());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
