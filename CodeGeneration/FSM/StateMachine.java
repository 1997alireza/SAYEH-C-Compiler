package src.CodeGeneration.FSM;

import org.statefulj.persistence.annotations.State;
import src.Token.IdentifierToken;
import src.Token.Token;

import java.util.ArrayList;
import java.util.Stack;

public class StateMachine {
    @State
    String state;

    public ArrayList<Token> expression = new ArrayList<>();
    public Stack<IdentifierToken> variables = new Stack<>();

    public String getState(){
        return state;
    }

    public enum Event {
        UNKNOWN("unk")/*For unknown tokens*/,
        KEYWORD_VAR("var")/*int, char, bool*/,
        ASSIGN_OPERATOR("="),
        ADVANCED_ASSIGN_OPERATOR(".=")/*+=, -=, *=, /=*/,
        COMPUTABLE_OPERATOR("co")/*&&, ||, !, ==, !=, >, <, >=, <=, +, -, *, /, ++, --*/,
        IDENTIFIER("iden"),
        VALUE("val")/*true, false, character, number*/,
        COMMA(","),
        SEMICOLON(";");

        private String str;
        Event(String str){
            this.str = str;
        }
        public String toString(){
            return this.str;
        }

        //TODO: add remaining events, such as if, while, bracket, ...
    }

    public enum Action {
        ADD_IDENTIFIER_TO_VAR_STACK("a1"),
        DECLARE_IDENTIFIERS("a2"),
        ADD_EXPRESSION_TO_EXP_STACK("a3"),
        CALCULATE_EXPRESSION_AND_DECLARE_AND_INITIALIZE_IDENTIFIERS("a4"),

        CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER("a5"),

        ADD_IDENTIFIER_TO_EXP_STACK("a6"), // and open parenthesis
        ADD_CLOSE_PARENTHESIS_AND_CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER("a7");

        private String str;
        Action(String str){
            this.str = str;
        }
        public String toString(){
            return this.str;
        }
    }
}
