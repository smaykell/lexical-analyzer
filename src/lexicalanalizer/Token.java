package lexicalanalizer;

public class Token {

    private String token;
    private String lexeme;
    private int line;
    private int column;

    public Token(String token, String lexeme, int line, int column) {
        this.token = token;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "Token{" + "token=" + token + ", lexeme=" + lexeme + ", line=" + line + ", column=" + column + '}';
    }

}
