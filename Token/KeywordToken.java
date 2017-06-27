package src.Token;

import src.CodeGeneration.FSM.StateMachine;

public class KeywordToken extends Token {
    public KeywordToken(String value) {
        super(TOKEN_TYPE.Keyword, value);
    }

    @Override
    public StateMachine.Event getEvent() {
        if(value.equals("int") || value.equals("char") || value.equals("bool")){
            return StateMachine.Event.KEYWORD_VAR;
        }

        if(value.equals("true") || value.equals("false")){
            return StateMachine.Event.VALUE;
        }

        //TODO: add remaining cases, such as if, else, while

        return StateMachine.Event.UNKNOWN;
    }
}
