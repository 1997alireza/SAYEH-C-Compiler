package src.Token;

public class OperatorToken extends Token {
    public OperatorToken(String value) {
        super(TOKEN_TYPE.Operator, value);
    }
}
