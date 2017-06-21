package src.Token;

public class KeywordToken extends Token {
    public KeywordToken(String value) {
        super(TOKEN_TYPE.Keyword, value);
    }
}
