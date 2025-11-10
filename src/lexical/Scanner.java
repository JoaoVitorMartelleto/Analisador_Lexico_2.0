package lexical;

import util.TokenType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Scanner {
    private char[] source;
    private int pos;
    private int line;
    private int column;
    private Map<String, TokenType> keywords = new HashMap<>();

    public Scanner(String filename) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            this.source = content.toCharArray();
            this.pos = 0;
            this.line = 1;
            this.column = 1;
            initKeywords();
        } catch (IOException e) {
            e.printStackTrace();
            this.source = new char[0];
        }
    }

    private void initKeywords() {
        keywords.put("main", TokenType.KW_MAIN);
        keywords.put("var", TokenType.KW_VAR);
        keywords.put("int", TokenType.KW_INT);
        keywords.put("real", TokenType.KW_REAL);
        keywords.put("input", TokenType.KW_INPUT);
        keywords.put("print", TokenType.KW_PRINT);
        keywords.put("if", TokenType.KW_IF);
        keywords.put("then", TokenType.KW_THEN);
        keywords.put("else", TokenType.KW_ELSE);
        keywords.put("while", TokenType.KW_WHILE);
        keywords.put("E", TokenType.KW_E);
        keywords.put("OU", TokenType.KW_OU);
        keywords.put("NAO", TokenType.KW_NAO);
        keywords.put("e", TokenType.KW_E);
        keywords.put("ou", TokenType.KW_OU);
        keywords.put("nao", TokenType.KW_NAO);
    }

    public Token nextToken() {
        skipWhitespaceAndComments();
        if (isEoF()) return new Token(TokenType.EOF, "", line, column);

        int tokenLine = line;
        int tokenCol = column;
        char c = advance();

        if (isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (isLetter(peek()) || isDigit(peek()) || peek() == '_') {
                sb.append(advance());
            }
            String lexeme = sb.toString();
            TokenType kw = keywords.get(lexeme);
            if (kw != null) {
                return new Token(kw, lexeme, tokenLine, tokenCol);
            }
            return new Token(TokenType.IDENTIFIER, lexeme, tokenLine, tokenCol);
        }

        if (c == '.' || isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            if (c == '.') {
                if (!isDigit(peek())) {
                    throw new LexicalException("Literal numérico inválido: '.' sem dígitos", tokenLine, tokenCol);
                }
                sb.append('.');
                while (isDigit(peek())) sb.append(advance());
                return new Token(TokenType.FLOAT_LITERAL, sb.toString(), tokenLine, tokenCol);
            } else {
                sb.append(c);
                while (isDigit(peek())) sb.append(advance());
                if (peek() == '.') {
                    if (!isDigit(peekNext())) {
                        throw new LexicalException("Literal numérico inválido: ponto sem dígitos depois", tokenLine, tokenCol + sb.length());
                    }
                    sb.append(advance());
                    while (isDigit(peek())) sb.append(advance());
                    return new Token(TokenType.FLOAT_LITERAL, sb.toString(), tokenLine, tokenCol);
                } else {
                    return new Token(TokenType.INT_LITERAL, sb.toString(), tokenLine, tokenCol);
                }
            }
        }

        if (c == '"') {
            StringBuilder sb = new StringBuilder();
            while (!isEoF() && peek() != '"') {
                char ch = advance();
                if (ch == '\\' && !isEoF()) {
                    char esc = advance();
                    switch (esc) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(ch);
                }
            }
            if (peek() != '"') {
                throw new LexicalException("Cadeia não fechada", tokenLine, tokenCol);
            }
            advance();
            return new Token(TokenType.CADEIA, sb.toString(), tokenLine, tokenCol);
        }

        switch (c) {
            case '+':
                if (peek() == '+') { advance(); return new Token(TokenType.PLUSPLUS, "++", tokenLine, tokenCol); }
                return new Token(TokenType.PLUS, "+", tokenLine, tokenCol);
            case '-':
                if (peek() == '-') { advance(); return new Token(TokenType.MINUSMINUS, "--", tokenLine, tokenCol); }
                return new Token(TokenType.MINUS, "-", tokenLine, tokenCol);
            case '*': return new Token(TokenType.STAR, "*", tokenLine, tokenCol);
            case '/':
                return new Token(TokenType.SLASH, "/", tokenLine, tokenCol);
            case '=':
                if (peek() == '=') { advance(); return new Token(TokenType.EQ, "==", tokenLine, tokenCol); }
                return new Token(TokenType.ASSIGN, "=", tokenLine, tokenCol);
            case '>':
                if (peek() == '=') { advance(); return new Token(TokenType.GTE, ">=", tokenLine, tokenCol); }
                return new Token(TokenType.GT, ">", tokenLine, tokenCol);
            case '<':
                if (peek() == '-') { advance(); return new Token(TokenType.ASSIGN_ARROW, "<-", tokenLine, tokenCol); }
                if (peek() == '=') { advance(); return new Token(TokenType.LTE, "<=", tokenLine, tokenCol); }
                return new Token(TokenType.LT, "<", tokenLine, tokenCol);
            case '!':
                if (peek() == '=') { advance(); return new Token(TokenType.NEQ, "!=", tokenLine, tokenCol); }
                throw new LexicalException("Símbolo inválido: '!'", tokenLine, tokenCol);
            case '(': return new Token(TokenType.LPAREN, "(", tokenLine, tokenCol);
            case ')': return new Token(TokenType.RPAREN, ")", tokenLine, tokenCol);
            case '{': return new Token(TokenType.LBRACE, "{", tokenLine, tokenCol);
            case '}': return new Token(TokenType.RBRACE, "}", tokenLine, tokenCol);
            case ':': return new Token(TokenType.COLON, ":", tokenLine, tokenCol);
            case ';': return new Token(TokenType.SEMICOLON, ";", tokenLine, tokenCol);
            default:
                throw new LexicalException("Símbolo inválido: '" + c + "'", tokenLine, tokenCol);
        }
    }

    private void skipWhitespaceAndComments() {
        while (!isEoF()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
                continue;
            }
            if (c == '#') {
                while (!isEoF() && peek() != '\n') advance();
                continue;
            }
            if (c == '/' && peekNext() == '*') {
                advance(); advance();
                boolean closed = false;
                while (!isEoF()) {
                    if (peek() == '*' && peekNext() == '/') {
                        advance(); advance();
                        closed = true;
                        break;
                    } else {
                        advance();
                    }
                }
                if (!closed) {
                    throw new LexicalException("Comentário de bloco não fechado (esperado '*/')", line, column);
                }
                continue;
            }
            break;
        }
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private char peek() {
        if (pos >= source.length) return '\0';
        return source[pos];
    }

    private char peekNext() {
        if (pos + 1 >= source.length) return '\0';
        return source[pos + 1];
    }

    private char advance() {
        if (pos >= source.length) return '\0';
        char c = source[pos++];
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private boolean isEoF() {
        return pos >= source.length;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
}
