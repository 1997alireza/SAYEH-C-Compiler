package src.Analyze.Lexical;

import src.Token.*;

class Tokenizer {
    static Token buildToken(String data){
        switch (TokenIdentification.getTokenType(data)){
            case Character:
                return new CharacterToken(data);
            case Keyword:
                return new KeywordToken(data);
            case Identifier:
                return new IdentifierToken(data, -1, -1);
            case Number:
                return new NumberToken(new Integer(data), -1 ,-1);
            case Operator:
                return new OperatorToken(data);
            case Punctuation:
                return new PunctuationToken(data);
        }

        return new Token(Token.TOKEN_TYPE.Unknown, data);
    }
}
