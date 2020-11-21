package lexicalanalizer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LexicalAnalyzer {

    private final String[] reservedWords;
    private final String[] operators;
    private final String[] punctuationSymbols;
    private File fSourceCode;
    private Scanner scanner;
    boolean isString;

    public LexicalAnalyzer() {
        reservedWords = new String[]{"import", "package", "public", "private", "final", "class", "this", "void", "static", "for", "while", "if", "else", "switch", "case", "break", "continue", "return", "int", "double"};
        operators = new String[]{"+", "-", "*", "/", "^", "=", "<", ">", ">=", "<=", "==", "!="};
        punctuationSymbols = new String[]{".", ",", ";", "{", "}", "[", "]", "(", ")", ":", "\""};
    }

    public void setSourceCode(File sourceCode) {
        this.fSourceCode = sourceCode;
    }

    public List<Token> analyze() throws Exception {
        List tokens = new ArrayList<Token>();
        scanner = new Scanner(fSourceCode);
        String line;
        String word;
        int numberLine = 1;
        int numberColumn;
        boolean isSuspicious;

        while (scanner.hasNext()) {
            word = "";
            numberColumn = 0;
            isSuspicious = false;
            isString = false;
            line = scanner.nextLine();

            if (!line.trim().startsWith("//")) {

                System.out.println("Analizando linea " + numberLine);

                for (int i = 0; i < line.length(); i++) {

                    System.out.println("Caracter encontrado : " + line.charAt(i));

                    //inicio de palabra "
                    if (line.charAt(i) == 34 && isString == false) {
                        System.out.println("    Apertura de constante: " + line.charAt(i));
                        isString = true;
                        word += line.charAt(i);
                        tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                        word = "";
                        // espacio
                    } else if (line.charAt(i) == 32) {

                        // bandera de palabra esta activa
                        if (isString) {

                            // cierre de cadena "
                            if (line.charAt(i) == 34) {
                                System.out.println("    Cierre de constante 1: " + line.charAt(i));
                                // se guarda la palabra en la lista
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = line.charAt(i) + "";
                                //guardamos "
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = "";
                                //desactivamos la bandera de palabra
                                isString = false;

                            } else {
                                // se sigue concatenando
                                word += line.charAt(i);
                            }

                            // guardo la palabra
                        } else if (!word.isBlank()) {
                            tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                            word = "";
                        }

                    } else {
                        // bandera de palabra esta activa
                        if (isString) {
                            // cierre de cadena "
                            if (line.charAt(i) == 34) {
                                System.out.println("    Cierre de constante 2 :" + line.charAt(i));

                                // se guarda la palabra y "
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = line.charAt(i) + "";
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = "";
                                isString = false;
                            } else {
                                //  se sigue concatenando
                                word += line.charAt(i);
                            }

                            // es sospechosa cuando se encontro < > = ! y se espera =
                        } else if (isSuspicious) {
                            if (line.charAt(i) == 61) {
                                System.out.println("-->" + line.charAt(i));
                                word += line.charAt(i);
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = "";
                            } else {
                                if (!(line.charAt(i) > 59 && line.charAt(i) < 63) || line.charAt(i) != 33) {
                                    if (!word.isBlank()) {
                                        tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                    }
                                    word = line.charAt(i) + "";
                                } else {
                                    word += line.charAt(i);
                                }
                            }
                            isSuspicious = false;
                        } else {
                            if ((line.charAt(i) > 59 && line.charAt(i) < 63) || line.charAt(i) == 33) {
                                if (!word.isBlank()) {
                                    tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                    word = "";
                                }
                                isSuspicious = true;
                                word += line.charAt(i);
                            } else if (isPunctuationSymbols(line.charAt(i) + "")) {
                                if (!word.isBlank()) {
                                    tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                }
                                word = line.charAt(i) + "";
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = "";
                            } else if (isOperator(line.charAt(i) + "")) {
                                word = line.charAt(i) + "";
                                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
                                word = "";
                            } else {
                                word += line.charAt(i);
                            }
                        }
                    }
                    numberColumn++;
                }
            } else {
                System.out.println("Comentatio ignorado");
            }

            if (!word.isBlank()) {
                tokens.add(new Token(word, getTypeLexeme(word), numberLine, numberColumn));
            }
            numberLine++;
        }
        return tokens;

    }

    public String getTypeLexeme(String word) {

        System.out.println(">>" + word + "<<");

        if (isReservedWord(word)) {
            System.out.println("Word " + word);
            if (isString) {
                return "CONSTANT";
            }

            return word.toUpperCase();
        }

        if (isIdentifier(word)) {
            return "ID";
        }
        if (isOperator(word)) {

            switch (word) {
                case "+":
                    return "PLUS";
                case "-":
                    return "MINUS";
                case "*":
                    return "TIMES";
                case "/":
                    return "DIVIDE";
                case "^":
                    return "PLUS";
                case "=":
                    return "TO_ASSIGN";
                case "<":
                    return "LESS";
                case ">":
                    return "HIGHER";
                case ">=":
                    return "GREATER_EQUAL";
                case "<=":
                    return "LESS_EQUAL";
                case "==":
                    return "EQUAL";
                case "!=":
                    return "DIFFERENT";
                default:
                    return "OPERATOR";
            }

        }
        if (isPunctuationSymbols(word)) {
            return "PUNCTUATION_SYMBOL";
        }
        if (isConstant(word) || isString) {
            return "CONSTANT";
        }

        return "ERROR";
    }

    public boolean isIdentifier(String word) {
        word = word.replaceAll("^\\s*", "");
        return word.matches("^[a-zA-Z][a-zA-Z\\d*]*");
    }

    public boolean isConstant(String word) {
        return word.matches("^[-+]?\\d*\\.?\\d*$");
    }

    public boolean isReservedWord(String word) {
        word = word.replaceAll("^\\s*", "");

        for (String reservedWord : this.reservedWords) {
            if (word.equals(reservedWord)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOperator(String word) {
        for (String operator : this.operators) {
            if (word.equals(operator)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPunctuationSymbols(String word) {
        for (String punctuationSymbol : this.punctuationSymbols) {
            if (word.equals(punctuationSymbol)) {
                return true;
            }
        }
        return false;
    }

}
