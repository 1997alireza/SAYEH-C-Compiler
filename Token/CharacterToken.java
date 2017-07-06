package src.Token;

import src.CodeGenerationAndSyntaxChecker.FSM.StateMachine;

public class CharacterToken extends Token {

    public char character;
    public CharacterToken(String value) {
        super(TOKEN_TYPE.Character, value);
        character = value.charAt(1);
    }

    @Override
    public StateMachine.Event getEvent() {
        return StateMachine.Event.VALUE;
    }
}
