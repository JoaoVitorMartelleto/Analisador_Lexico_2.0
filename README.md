# 📖 Analisador Léxico – Checkpoint 02  

Projeto desenvolvido para a disciplina Construção de Compiladores, como continuação do Checkpoint 01 (Analisador Léxico).   
Neste checkpoint foi implementado o Analisador Sintático, responsável por validar a estrutura gramatical de programas escritos na linguagem definida pelo Professor.

---

## 🚀 Funcionalidades implementadas  

📢​ Identifica tokens:   

- Palavras reservadas, identificadores, números inteiros e reais, operadores e delimitadores;   
- Realiza tratamento de erros léxicos com número de linha e coluna;   
- Suporta comentários de linha e bloco;   
- Integra-se com o analisador sintático via classe Scanner.   

📢​ Analisador Sintático :  

- Implementa um parser recursivo-descendente em Java;   
- Segue a gramática livre de contexto definida para a linguagem;   
- Detecta erros sintáticos e exibe mensagens descritivas com posição no código;   
- Reconhece:   
-- Declarações de variáveis (var { ... });   
-- Estruturas condicionais (if, else);   
-- Estruturas de repetição (while);   
-- Entrada (input) e saída (print);   
-- Expressões aritméticas, relacionais e lógicas (E, OU, NAO);   
-- Operadores de incremento e decremento (++, --);   
-- Blocos de comandos delimitados por { ... }.   

---

## 📂 Estrutura do projeto  
```c
Analisador_Lexico/
│── src/
│ ├── lexical/
│ │ ├── Scanner.java
│ │ ├── Token.java
│ │ ├── LexicalException.java
│ │ ├── SyntaxException.java
│ │ └── Parser.java
│ ├── util/
│ │ └── TokenType.java
│ ├── mini_compiler/
│ │ └── Main.java
│── programa_ckp2_qui_noite.txt 
│── .gitignore

```

---

## ▶️ Como executar  

### 1. Compilar os arquivos
No terminal, dentro da raiz do projeto:  

```bash
javac util/*.java lexical/*.java mini_compiler/*.java
```
```bash
java mini_compiler.Main
```
---
## 📝 Exemplo de entrada (programa.mc)

```c
main {
    var {
        x:int;
        temp:int;
    }

    input(x);
    if (x > z E y <= z OU x != z) then {
        temp <- z;
        z <- x;
        x <- temp;
    }

    print("Ordem crescente:");
    print(x);
}
```

---

## 💻 Exemplo de saída

```c
Token[type=KW_MAIN, lexeme='main', line=1, col=1]
Token[type=LBRACE, lexeme='{', line=1, col=6]
Token[type=KW_VAR, lexeme='var', line=2, col=5]
...
Token[type=KW_PRINT, lexeme='print', line=36, col=5]
Token[type=EOF, lexeme='', line=37, col=1]

Parse concluído com sucesso!
```
---

## 👨‍💻 Integrantes
João Victor Martelletto de Paula Teixeira
