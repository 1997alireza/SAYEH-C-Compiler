package src;

import src.Analyze.Lexical.LexicalAnalyzer;
import src.CodeGeneration.CodeGenerator;


public class Main {
    public static void main(String[] args) {
        final String sourceDir = "C:\\Users\\AliReza\\IdeaProjects\\Sayeh\\res\\";

        LexicalAnalyzer.removeComments(sourceDir, "test1.c");
        LexicalAnalyzer.tokenize();
        CodeGenerator.getGenerator().generate();

        for(String op : ClassifiedData.getInstance().opcodes) {
            System.out.printf(op);
        }


    }
}
