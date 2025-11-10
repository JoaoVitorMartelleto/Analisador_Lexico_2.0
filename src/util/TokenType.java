package util;

public enum TokenType {
    IDENTIFIER,
    INT_LITERAL,
    FLOAT_LITERAL,
    CADEIA,

    PLUS, MINUS, STAR, SLASH,
    PLUSPLUS, MINUSMINUS,

    ASSIGN,
    ASSIGN_ARROW,

    COLON,
    SEMICOLON,
    LBRACE, RBRACE,
    LPAREN, RPAREN,

    GT, GTE, LT, LTE, NEQ, EQ,

    KW_MAIN,
    KW_VAR,
    KW_INT,
    KW_REAL,
    KW_INPUT,
    KW_PRINT,
    KW_IF,
    KW_THEN,
    KW_ELSE,
    KW_WHILE,

    KW_E, KW_OU, KW_NAO,

    EOF
}