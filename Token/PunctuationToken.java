package src.Token;

public class PunctuationToken extends Token{
    public PunctuationToken(String value) {
        super(TOKEN_TYPE.Punctuation, value);
    }
}
