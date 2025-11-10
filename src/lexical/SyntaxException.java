package lexical;

public class SyntaxException extends RuntimeException {
    private final int line;
    private final int column;

    public SyntaxException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return String.format("Erro sintático na linha %d, coluna %d: %s", line, column, getMessage());
    }
}