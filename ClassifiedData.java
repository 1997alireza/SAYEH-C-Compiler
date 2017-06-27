package src;

import src.Analyze.Utilies.CFileReader;
import src.CodeGeneration.OPCode;
import src.Token.Token;

import java.util.ArrayList;

public class ClassifiedData {
    private static ClassifiedData instance;
    public CFileReader fileReader;
    public ArrayList<Token> tokens;
    public ArrayList<String> opcodes;
    private ClassifiedData(){
        instance = this;
        tokens = new ArrayList<Token>();
        opcodes = new ArrayList<>();

        setupInitializeOpcodes();
    }
    public static ClassifiedData getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new ClassifiedData();
    }

    private void setupInitializeOpcodes(){ // clear WP and set to "000"
        isAn8bitInstructionWaiting = true;
        _8bitWaitingInstruction = OPCode.getOpcode(OPCode.OPCODE_8.CWP);
    }

    private boolean isAn8bitInstructionWaiting;
    private String _8bitWaitingInstruction;
    public void addOpcode(String opcode){ // to concat 8bit instructions and store them in one sel of the memory
        if(opcode.length() == 8){
            if(isAn8bitInstructionWaiting){
                opcodes.add(_8bitWaitingInstruction + opcode);
                isAn8bitInstructionWaiting = false;
            }
            else {
                isAn8bitInstructionWaiting = true;
                _8bitWaitingInstruction = opcode;
            }
        }
        else /*is 16*/ {
            if(isAn8bitInstructionWaiting){
                opcodes.add(_8bitWaitingInstruction + "00000000");
                isAn8bitInstructionWaiting = false;
            }
            opcodes.add(opcode);
        }
    }
    public void onEndOfTokens(){
        if(isAn8bitInstructionWaiting) {
            opcodes.add(_8bitWaitingInstruction + "00000000");
            isAn8bitInstructionWaiting = false;
        }
    }
}
