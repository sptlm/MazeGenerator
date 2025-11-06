package academy.model;

public record Point(int x, int y) {
    public int manhattanDistance(Point other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
