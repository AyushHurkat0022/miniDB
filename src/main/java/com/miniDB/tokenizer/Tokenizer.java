package com.minidb.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private final String input;
    private int position;

    public Tokenizer(String input){
        this.input = input;
        this.position = 0;
    }

//    public static void main(String [] args){
//        Tokenizer t = new Tokenizer("SELECT name, salary FROM employees WHERE salary>50000");
//        for(Token token: t.tokenize()){
//            System.out.println(token);
//        }
//    }

    public List<Token> tokenize(){
        List<Token> tokens = new ArrayList<>();

        while (position<input.length()){
            char current = input.charAt(position);

            if(Character.isWhitespace(current)){
                position++;
                continue;
            }

            if(Character.isLetter(current) || current == '_'){
                tokens.add(readWord());
                continue;
            }

            if(Character.isDigit(current)){
                tokens.add(readNumber());
                continue;
            }

            if (current == '\''){
                tokens.add(readString());
                continue;
            }

            if(isSymbol(current)){
                tokens.add(readSymbol());
                continue;
            }

            throw new IllegalArgumentException(
                    "Unexpected character '" + current + "' at position " + position
            );
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private Token readWord(){
        int start = position;
        while(position<input.length()
                && (Character.isLetterOrDigit(input.charAt(position))
                    || input.charAt(position) == '_')){
            position++;
        }

        String word = input.substring(start, position);

        if(Keywords.isKeyword(word)){
            return new Token(TokenType.KEYWORD, word.toUpperCase());
        }
        return new Token(TokenType.IDENTIFIER, word);
    }

    private Token readNumber(){
        int start = position;
        while (position<input.length()
                && (Character.isDigit(input.charAt(position))
                    || input.charAt(position) == '.')){
            position++;
        }

        String num = input.substring(start, position);
        return new Token(TokenType.NUMBER, num);
    }

    private Token readString(){
        position++;
        int start = position;
        while (position<input.length()
                && input.charAt(position) != '\''){
            position++;
        }

        if(position >= input.length()){
            throw new IllegalArgumentException("Unterminated String Starting near position "+ start);
        }

        String value = input.substring(start, position);
        position++;
        return new Token(TokenType.STRING, value);
    }

    private boolean isSymbol(char c){
        return "*(),;=<>.".indexOf(c) >= 0;
    }

    private Token readSymbol(){
        char c = input.charAt(position);

        if((c == '>' || c == '<') && position+1 < input.length() && input.charAt(position+1) == '='){
            String symbol = ""+c+'=';
            position+=2;
            return new Token(TokenType.SYMBOL, symbol);
        }
        position++;
        return new Token(TokenType.SYMBOL, String.valueOf(c));
    }
}
