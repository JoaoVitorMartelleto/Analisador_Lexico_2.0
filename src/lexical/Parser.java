package lexical;

import util.TokenType;

public class Parser {
    private Scanner scanner;
    private Token current;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        advance();
    }

    private void advance() {
        current = scanner.nextToken();
    }

    private void expect(TokenType type) {
        if (current != null && current.getType() == type) {
            advance();
        } else {
            int line = (current != null) ? current.getLine() : scanner.getLine();
            int col = (current != null) ? current.getColumn() : scanner.getColumn();
            String found = (current != null) ? current.getType().toString() : "EOF";
            throw new SyntaxException(String.format("Esperado %s mas encontrado %s", type, found), line, col);
        }
    }

    public void parseProgram() {
        expect(TokenType.KW_MAIN);
        expect(TokenType.LBRACE);
        parseCorpo();
        expect(TokenType.RBRACE);
        if (current.getType() != TokenType.EOF) {
            throw new SyntaxException("Conteúdo após fim do programa", current.getLine(), current.getColumn());
        }
    }

    private void parseCorpo() {
        if (current.getType() == TokenType.KW_VAR) {
            parseSecaoDeclaracoes();
        }
        parseListaComandos();
    }

    private void parseSecaoDeclaracoes() {
        expect(TokenType.KW_VAR);
        expect(TokenType.LBRACE);
        parseListaDeclaracoes();
        expect(TokenType.RBRACE);
    }

    private void parseListaDeclaracoes() {
        while (current.getType() == TokenType.IDENTIFIER) {
            parseDeclaracao();
        }
    }

    private void parseDeclaracao() {
        if (current.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Esperado identificador em declaracao", current.getLine(), current.getColumn());
        }
        advance(); // ID
        expect(TokenType.COLON);
        parseTipo();
        expect(TokenType.SEMICOLON);
    }

    private void parseTipo() {
        if (current.getType() == TokenType.KW_INT) advance();
        else if (current.getType() == TokenType.KW_REAL) advance();
        else throw new SyntaxException("Esperado tipo 'int' ou 'real'", current.getLine(), current.getColumn());
    }

    private void parseListaComandos() {
        while (isStartOfCommand(current.getType())) {
            parseComando();
        }
    }

    private boolean isStartOfCommand(util.TokenType t) {
        switch (t) {
            case IDENTIFIER:
            case KW_INPUT:
            case KW_PRINT:
            case KW_IF:
            case KW_WHILE:
            case LBRACE:
                return true;
            default:
                return false;
        }
    }

    private void parseComando() {
        switch (current.getType()) {
            case IDENTIFIER:
                parseAtribuicaoOrIncDec();
                break;
            case KW_INPUT:
                parseLeitura();
                break;
            case KW_PRINT:
                parseEscrita();
                break;
            case KW_IF:
                parseCondicional();
                break;
            case KW_WHILE:
                parseRepeticao();
                break;
            case LBRACE:
                parseBloco();
                break;
            default:
                throw new SyntaxException("Comando inválido", current.getLine(), current.getColumn());
        }
    }

    private void parseAtribuicaoOrIncDec() {
        Token id = current;
        expect(TokenType.IDENTIFIER);
        if (current.getType() == TokenType.ASSIGN_ARROW) {
            advance();
            parseExpressaoAritmetica();
            expect(TokenType.SEMICOLON);
        } else if (current.getType() == TokenType.PLUSPLUS) {
            advance();
            expect(TokenType.SEMICOLON);
        } else if (current.getType() == TokenType.MINUSMINUS) {
            advance();
            expect(TokenType.SEMICOLON);
        } else {
            throw new SyntaxException("Esperado '<-' ou '++' ou '--' em atribuicao/operador", current.getLine(), current.getColumn());
        }
    }

    private void parseLeitura() {
        expect(TokenType.KW_INPUT);
        expect(TokenType.LPAREN);
        if (current.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxException("Esperado identificador em input()", current.getLine(), current.getColumn());
        }
        advance();
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
    }

    private void parseEscrita() {
        expect(TokenType.KW_PRINT);
        expect(TokenType.LPAREN);
        if (current.getType() == TokenType.IDENTIFIER || current.getType() == TokenType.CADEIA) {
            advance();
        } else {
            throw new SyntaxException("Esperado identificador ou cadeia em print()", current.getLine(), current.getColumn());
        }
        expect(TokenType.RPAREN);
        expect(TokenType.SEMICOLON);
    }

    private void parseCondicional() {
        expect(TokenType.KW_IF);
        parseExpressaoRelacional();
        expect(TokenType.KW_THEN);
        parseComando();
        if (current.getType() == TokenType.KW_ELSE) {
            advance();
            parseComando();
        }
    }

    private void parseRepeticao() {
        expect(TokenType.KW_WHILE);
        parseExpressaoRelacional();
        parseComando();
    }

    private void parseBloco() {
        // '{' listaComandos '}'
        expect(TokenType.LBRACE);
        parseListaComandos();
        expect(TokenType.RBRACE);
    }

    private void parseExpressaoAritmetica() {
        parseTermo();
        while (current.getType() == TokenType.PLUS || current.getType() == TokenType.MINUS) {
            advance();
            parseTermo();
        }
    }

    private void parseTermo() {
        parseFator();
        while (current.getType() == TokenType.STAR || current.getType() == TokenType.SLASH) {
            advance();
            parseFator();
        }
    }

    private void parseFator() {
        if (current.getType() == TokenType.INT_LITERAL || current.getType() == TokenType.FLOAT_LITERAL) {
            advance();
        } else if (current.getType() == TokenType.IDENTIFIER) {
            advance();
            if (current.getType() == TokenType.PLUSPLUS || current.getType() == TokenType.MINUSMINUS) {
                advance();
            }
        } else if (current.getType() == TokenType.LPAREN) {
            advance();
            parseExpressaoAritmetica();
            expect(TokenType.RPAREN);
        } else {
            throw new SyntaxException("Esperado numero, identificador ou '(' em fator", current.getLine(), current.getColumn());
        }
    }

    private void parseExpressaoRelacional() {
        parseTermoRelacional();
        while (isOperadorLogico(current.getType())) {
            advance();
            parseTermoRelacional();
        }
    }

    private void parseTermoRelacional() {
        if (current.getType() == TokenType.LPAREN) {
            advance();
            parseExpressaoRelacional();
            expect(TokenType.RPAREN);
        } else {
            parseExpressaoAritmetica();
            if (!isOpRel(current.getType())) {
                throw new SyntaxException("Esperado operador relacional", current.getLine(), current.getColumn());
            }
            advance();
            parseExpressaoAritmetica();
        }
    }

    private boolean isOpRel(TokenType t) {
        return t == TokenType.GT || t == TokenType.GTE || t == TokenType.LT ||
                t == TokenType.LTE || t == TokenType.NEQ || t == TokenType.EQ;
    }

    private boolean isOperadorLogico(TokenType t) {
        return t == TokenType.KW_E || t == TokenType.KW_OU || t == TokenType.KW_NAO;
    }
}
