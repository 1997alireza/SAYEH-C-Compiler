package src.Token;

import src.CodeGenerationAndSyntaxChecker.FSM.StateMachine;

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

        if(value.equals("if")){
            return StateMachine.Event.IF;
        }

        if(value.equals("else")){
            return StateMachine.Event.ELSE;
        }

        if(value.equals("while")){
            return StateMachine.Event.WHILE;
        }

        return StateMachine.Event.UNKNOWN;
    }
}
