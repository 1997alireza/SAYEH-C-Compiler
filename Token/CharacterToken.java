package src.Token;

public class CharacterToken extends Token {

    char character;
    public CharacterToken(String value) {
        super(TOKEN_TYPE.Character, value);
        character = value.charAt(1);
    }
}
