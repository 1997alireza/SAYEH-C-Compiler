package src;

import src.Analyze.Lexical.LexicalAnalyzer;


public class Main {
    public static void main(String[] args) {
        final String sourceDir = "C:\\Users\\AliReza\\IdeaProjects\\Sayeh\\res\\";

        LexicalAnalyzer.removeComments(sourceDir, "test1.c");
        LexicalAnalyzer.tokenize();

        System.out.printf(ClassifiedData.getInstance().fileReader.fileContent);

    }
}
