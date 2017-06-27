package src.Token;

import src.CodeGeneration.FSM.StateMachine;

public class Token {
    public enum TOKEN_TYPE {Keyword, Identifier, Number, Character, Operator, Punctuation, Unknown};


    public TOKEN_TYPE type;
    public String value;

    public Token(TOKEN_TYPE type, String value){
        this.type = type;
        this.value = value;
    }

    public StateMachine.Event getEvent(){
        return StateMachine.Event.UNKNOWN;
    }
}

