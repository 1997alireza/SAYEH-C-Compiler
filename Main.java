package src;

import org.apache.log4j.BasicConfigurator;
import src.Analyze.Lexical.LexicalAnalyzer;
import src.CodeGeneration.CodeGenerator;
import src.CodeGeneration.FSM.StateMachine;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        final String sourceDir = "C:\\Users\\AliReza\\IdeaProjects\\Sayeh\\res\\";

        LexicalAnalyzer.removeComments(sourceDir, "test1.c");
        LexicalAnalyzer.tokenize();

        BasicConfigurator.configure();

        ArrayList<String> opcodes = new ArrayList<>();
        StateMachine theResult = (new CodeGenerator(ClassifiedData.getInstance().tokens,
                                    opcodes, "base generator")).generate();

        if(theResult.errorMsg == null) {
            ClassifiedData.getInstance().addOpcodes(opcodes);

            for (String op : ClassifiedData.getInstance().getOpcodes()) {
                System.out.println(op);
            }
        }
        else {
            System.err.println(theResult.errorMsg);
        }


    }
}
