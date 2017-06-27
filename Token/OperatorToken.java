package src.Token;

import src.CodeGeneration.FSM.StateMachine;

public class OperatorToken extends Token {
    public OperatorToken(String value) {
        super(TOKEN_TYPE.Operator, value);
    }

    @Override
    public StateMachine.Event getEvent() {
        if(value.equals("=")){
            return StateMachine.Event.ASSIGN_OPERATOR;
        }

        if(value.equals("+=") || value.equals("-=") || value.equals("*=") || value.equals("/=")){
            return StateMachine.Event.ADVANCED_ASSIGN_OPERATOR;
        }

        return StateMachine.Event.COMPUTABLE_OPERATOR;
    }
}
