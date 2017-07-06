package src.Token;

import src.CodeGenerationAndSyntaxChecker.FSM.StateMachine;

public class NumberToken extends Token {
    public int intValue;
    int registerAdr, memoryAdr;

    public NumberToken(int intValue, int registerAdr, int memoryAdr) {
        super(TOKEN_TYPE.Number, 'i' + String.valueOf(intValue));
        this.intValue = intValue;
        this.registerAdr = registerAdr;
        this.memoryAdr = memoryAdr;
    }

    @Override
    public StateMachine.Event getEvent() {
        return StateMachine.Event.VALUE;
    }
}
