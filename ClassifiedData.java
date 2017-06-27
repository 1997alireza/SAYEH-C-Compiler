package src;

import src.Analyze.Utilies.CFileReader;
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

        //TODO : initial opcodes
    }
    public static ClassifiedData getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new ClassifiedData();
    }
}
