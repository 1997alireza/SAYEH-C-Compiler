package src.Token;

public class NumberToken extends Token {
    int intValue;
    int registerAdr, memoryAdr;

    public NumberToken(int intValue, int registerAdr, int memoryAdr) {
        super(TOKEN_TYPE.Number, 'i' + String.valueOf(intValue));
        this.intValue = intValue;
        this.registerAdr = registerAdr;
        this.memoryAdr = memoryAdr;
    }
}
