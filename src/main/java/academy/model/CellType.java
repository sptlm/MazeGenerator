package academy.model;

public enum CellType {
    WALL('#', Integer.MAX_VALUE),
    PASSAGE(' ', 10),
    ASPHALT('=', 5),
    SAND('~', 15),
    SWAMP('*', 20);

    private final char symbol;
    private final int weight;

    CellType(char symbol, int weight) {
        this.symbol = symbol;
        this.weight = weight;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isPassable() {
        return this != WALL;
    }

    public static CellType fromSymbol(char symbol) {
        for (CellType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown cell type symbol: " + symbol);
    }
}
