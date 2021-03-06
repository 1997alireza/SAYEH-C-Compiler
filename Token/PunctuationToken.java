package src.Token;

import src.CodeGenerationAndSyntaxChecker.FSM.StateMachine;

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

        if(value.equals("(")){
            return StateMachine.Event.OPEN_PARENTHESIS;
        }

        if(value.equals(")")){
            return StateMachine.Event.CLOSE_PARENTHESIS;
        }

        if(value.equals("{")){
            return StateMachine.Event.OPEN_BRACE;
        }

        if(value.equals("}")){
            return StateMachine.Event.CLOSE_BRACE;
        }

        return StateMachine.Event.UNKNOWN;
    }
}
