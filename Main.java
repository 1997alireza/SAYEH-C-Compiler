package src;

import src.Analyze.Lexical.LexicalAnalyzer;
import src.CodeGeneration.CodeGenerator;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        final String sourceDir = "C:\\Users\\AliReza\\IdeaProjects\\Sayeh\\res\\";

        LexicalAnalyzer.removeComments(sourceDir, "test1.c");
        LexicalAnalyzer.tokenize();

        ArrayList<String> opcodes = new ArrayList<>();
        (new CodeGenerator(ClassifiedData.getInstance().tokens,
                                    opcodes, "base generator")).generate();
        ClassifiedData.getInstance().addOpcodes(opcodes);

        for(String op : ClassifiedData.getInstance().getOpcodes()) {
            System.out.println(op);
        }


    }
}
