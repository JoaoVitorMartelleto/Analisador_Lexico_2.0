package mini_compiler;

import lexical.*;
import util.TokenType;

public class Main {
    public static void main(String[] args) {
        String filename = "programa_ckp2_qui_noite.txt";
        Scanner sc = new Scanner(filename);

        System.out.println("----- Tokens -----");
        try {
            Token t;
            do {
                t = sc.nextToken();
                System.out.println(t);
            } while (t.getType() != TokenType.EOF);
        } catch (LexicalException e) {
            System.err.println(e.toString());
            return;
        }


        sc = new Scanner(filename);
        Parser parser = new Parser(sc);
        try {
            parser.parseProgram();
            System.out.println("Parse concluído com sucesso!");
        } catch (LexicalException le) {
            System.err.println(le.toString());
        } catch (SyntaxException se) {
            System.err.println(se.toString());
        } catch (RuntimeException re) {
            System.err.println("Erro: " + re.getMessage());
        }
    }
}