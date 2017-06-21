package src.Analyze.Lexical;

import com.commentremover.app.CommentProcessor;
import com.commentremover.app.CommentRemover;
import com.commentremover.exception.CommentRemoverException;
import src.Analyze.Utilies.CFileReader;
import src.ClassifiedData;
import src.Token.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class LexicalAnalyzer {
    public static void removeComments(String srcDir, String fileName){
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        String outputPath = srcDir + "temp\\" + fileName + ".java";
        try {
            sourceChannel = new FileInputStream(srcDir + fileName).getChannel();
            destChannel = new FileOutputStream(outputPath).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        CommentRemover commentRemover = new CommentRemover.CommentRemoverBuilder()
                .removeJava(true) // Remove Java file Comments....
                .removeJavaScript(true) // Remove JavaScript file Comments....
                .removeJSP(true) // etc.. goes like that
                .removeTodos(true) //  Remove Touch Todos
                .removeSingleLines(true) // Remove single line type comments
                .removeMultiLines(true) // Remove multiple type comments
                .preserveJavaClassHeaders(true) // Preserves class header comment
                .preserveCopyRightHeaders(true) // Preserves copyright comment
//                .startInternalPath("res")
//                .setExcludePackages(new String[]{"res"})
                .startExternalPath(outputPath)
                .build();

        CommentProcessor commentProcessor = new CommentProcessor(commentRemover);
        try {
            commentProcessor.start();
        } catch (CommentRemoverException e) {
            e.printStackTrace();
        }


        ClassifiedData.getInstance().fileReader = new CFileReader(outputPath);

    }

    public static void tokenize(){
        ArrayList<Token> tokens = ClassifiedData.getInstance().tokens;
        tokens.clear();
        for(String s : ClassifiedData.getInstance().fileReader.fileContent.split(" |\n|\t")){
            if(s != null && !s.equals("")){
                tokens.add(Tokenizer.buildToken(s));
            }
        }
    }
}
