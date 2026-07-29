package com.minidb.tokenizer;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        return type + "( " + value + " )";
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return this.type == other.type && this.value.equals(other.value);
    }

    @Override
    public int hashCode(){
        return java.util.Objects.hash(type, value);
    }
}
