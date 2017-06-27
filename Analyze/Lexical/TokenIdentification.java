package src.Analyze.Lexical;

import src.Token.Token;

public class TokenIdentification {

    public static final String [] KeywordTokens = {"if", "else", "while", "int", "char", "bool", "null", "true", "false"};
    public static final String [] OperatorTokens = {"=", "+=", "-=", "*=", "/=",
                                             "&&", "||", "!",
                                             "==", "!=", ">", "<", ">=", "<=",
                                             "+", "-", "*", "/", "++", "--"};
    public static final String [] PunctuationTokens = {",", ")", "(", "}", "{", ";"};

    public static Token.TOKEN_TYPE getTokenType(String token){
        if(token == null)
            return null;

        for(String t : KeywordTokens){
            if(t.equals(token))
                return Token.TOKEN_TYPE.Keyword;
        }

        for(String t : OperatorTokens){
            if(t.equals(token))
                return Token.TOKEN_TYPE.Operator;
        }

        for(String t : PunctuationTokens){
            if(t.equals(token))
                return Token.TOKEN_TYPE.Punctuation;
        }

        if(token.matches("'.'"))
            return Token.TOKEN_TYPE.Character;

        if(token.matches("^-?[0-9]+|^+?[0-9]+"))
            return Token.TOKEN_TYPE.Number;

        if(token.matches("^([a-z]|[A-Z])([a-z]|[A-Z]|[0-9])*"))
            return Token.TOKEN_TYPE.Identifier;

        return Token.TOKEN_TYPE.Unknown;
    }
}
