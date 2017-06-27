package src.Token;

import src.CodeGeneration.FSM.StateMachine;

public class PunctuationToken extends Token{
    public PunctuationToken(String value) {
        super(TOKEN_TYPE.Punctuation, value);
    }

    @Override
    public StateMachine.Event getEvent() {
        if(value.equals(";")) {
            return StateMachine.Event.SEMICOLON;
        }

        if(value.equals(",")) {
            return StateMachine.Event.COMMA;
        }

        //TODO: add remaining cases, such as bracket, ...

        return StateMachine.Event.UNKNOWN;
    }
}
