package src;

import src.Analyze.Utilies.CFileReader;
import src.Token.Token;

import java.util.ArrayList;

public class ClassifiedData {
    private static ClassifiedData instance;
    public CFileReader fileReader;
    public ArrayList<Token> tokens;
    private ClassifiedData(){
        instance = this;
        tokens = new ArrayList<Token>();
    }
    public static ClassifiedData getInstance(){
        if(instance != null){
            return instance;
        }
        return instance = new ClassifiedData();
    }
}
