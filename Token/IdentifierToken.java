package src.Token;

import src.CodeGeneration.FSM.StateMachine;

public class IdentifierToken extends Token {
    int registerAdr, memoryAdr;

    public IdentifierToken(String value, int registerAdr, int memoryAdr) {
        super(TOKEN_TYPE.Identifier, value);
        this.registerAdr = registerAdr;
        this.memoryAdr = memoryAdr;
    }

    @Override
    public StateMachine.Event getEvent() {
        return StateMachine.Event.IDENTIFIER;
    }
}