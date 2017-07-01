package src;

import src.Analyze.Utilies.CFileReader;
import src.CodeGeneration.OPCode;
import src.Token.Token;

import java.util.ArrayList;

public class ClassifiedData {
    private static ClassifiedData instance;
    public CFileReader fileReader;
    public ArrayList<Token> tokens;
    private ArrayList<String> opcodes;
    private ClassifiedData(){
        instance = this;
        tokens = new ArrayList<>();
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
        addOpcode(OPCode.getOpcode(OPCode.OPCODE_8.CWP));
    }

    private void addOpcode(String opcode){ // to concat 8bit instructions and store them in one sel of the memory
        if(opcode.length() == 8){
            opcodes.add("00000000" + opcode);
        }
        else /*is 16*/ {
            opcodes.add(opcode);
        }
    }

    public void addOpcodes(ArrayList<String> opcodes){
        for(String op : opcodes){
            addOpcode(op);
        }
    }

    public ArrayList<String> getOpcodes(){
        return opcodes;
    }
}
