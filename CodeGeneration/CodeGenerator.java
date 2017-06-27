package src.CodeGeneration;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.TooBusyException;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.fsm.model.State;
import org.statefulj.persistence.memory.MemoryPersisterImpl;
import src.ClassifiedData;
import src.CodeGeneration.FSM.StateMachine;
import src.Token.IdentifierToken;
import src.Token.OperatorToken;
import src.Token.PunctuationToken;
import src.Token.Token;

import java.util.LinkedList;
import java.util.List;

public class CodeGenerator {


    private List<State<StateMachine>> states;
    private MemoryPersisterImpl<StateMachine> persister;
    private State<StateMachine> start;
    private State<StateMachine> def;
    private State<StateMachine> def1;
    private State<StateMachine> def2;
    private State<StateMachine> def3;
    private State<StateMachine> asg;
    private State<StateMachine> asg1;
    private State<StateMachine> asg2;
    private State<StateMachine> advAsg1;
    private State<StateMachine> advAsg2;

    private FSM fsm;

    private static CodeGenerator instance = null;
    public static CodeGenerator getGenerator(){
        if(instance == null){
            instance = new CodeGenerator();
        }
        return instance;
    }

    private CodeGenerator(){
        states = new LinkedList<>();

        start = new StateImpl<>("start");
        def = new StateImpl<>("def");
        def1 = new StateImpl<>("def 1");
        def2 = new StateImpl<>("def 2");
        def3 = new StateImpl<>("def 3");
        asg = new StateImpl<>("asg 0");
        asg1 = new StateImpl<>("asg 1");
        asg2 = new StateImpl<>("asg 2");
        advAsg1 = new StateImpl<>("adv asg 1");
        advAsg2 = new StateImpl<>("adv asg 2");

        start.addTransition(StateMachine.Event.KEYWORD_VAR.toString(), def);
        start.addTransition(StateMachine.Event.IDENTIFIER.toString(), asg,
                new theAction<>(StateMachine.Action.ADD_IDENTIFIER_TO_VAR_STACK));

        def.addTransition(StateMachine.Event.IDENTIFIER.toString(), def1,
                new theAction<>(StateMachine.Action.ADD_IDENTIFIER_TO_VAR_STACK));

        def1.addTransition(StateMachine.Event.COMMA.toString(), def);
        def1.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.DECLARE_IDENTIFIERS));
        def1.addTransition(StateMachine.Event.ASSIGN_OPERATOR.toString(), def2);

        def2.addTransition(StateMachine.Event.VALUE.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        def3.addTransition(StateMachine.Event.VALUE.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        def3.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        def3.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.CALCULATE_EXPRESSION_AND_DECLARE_AND_INITIALIZE_IDENTIFIERS));

        asg.addTransition(StateMachine.Event.ASSIGN_OPERATOR.toString(), asg1);
        asg.addTransition(StateMachine.Event.ADVANCED_ASSIGN_OPERATOR.toString(), advAsg1,
                new theAction<>(StateMachine.Action.ADD_IDENTIFIER_TO_EXP_STACK));

        asg1.addTransition(StateMachine.Event.VALUE.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        asg2.addTransition(StateMachine.Event.VALUE.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER));

        advAsg1.addTransition(StateMachine.Event.VALUE.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        advAsg2.addTransition(StateMachine.Event.VALUE.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.ADD_CLOSE_PARENTHESIS_AND_CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER));




        states.add(start);
        states.add(def);
        states.add(def1);
        states.add(def2);
        states.add(def3);
        states.add(asg);
        states.add(asg1);
        states.add(asg2);
        states.add(advAsg1);
        states.add(advAsg2);
        persister = new MemoryPersisterImpl<>(states, start);
        fsm = new FSM("code generator", persister);
    }

    private static class theAction<T> implements Action<T> {

        StateMachine.Action action;
        private theAction(StateMachine.Action action){
            this.action = action;
        }


        @Override
        public void execute(T stateful, String event, Object... args) throws RetryException {
            Token token = (Token) args[0];
            StateMachine inProcessFSM = (StateMachine) stateful;
            switch (action){
                case ADD_IDENTIFIER_TO_VAR_STACK:
                    inProcessFSM.variables.add((IdentifierToken) token);
                    break;
                case DECLARE_IDENTIFIERS:
                    while(!inProcessFSM.variables.isEmpty()){
                        Memory.getRAM().aloc(inProcessFSM.variables.pop().value);
                    }
                    break;
                case ADD_EXPRESSION_TO_EXP_STACK:
                    inProcessFSM.expression.add(token);
                    break;
                case CALCULATE_EXPRESSION_AND_DECLARE_AND_INITIALIZE_IDENTIFIERS:
                    int resultPlace = OPCodeGenerator.calculateExpression(inProcessFSM.expression);
                    OPCodeGenerator.loadMem(resultPlace, "00");
                    while(!inProcessFSM.variables.isEmpty()){
                        String memSelName = inProcessFSM.variables.pop().value;
                        Memory.getRAM().aloc(memSelName);
                        OPCodeGenerator.storeMem(memSelName, "00");
                    }
                    break;
                case CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER:
                    int resultPlace2 = OPCodeGenerator.calculateExpression(inProcessFSM.expression);
                    OPCodeGenerator.loadMem(resultPlace2, "00");
                    String memSelName = inProcessFSM.variables.pop().value;
                    OPCodeGenerator.storeMem(memSelName, "00");
                    break;
                case ADD_IDENTIFIER_TO_EXP_STACK:
                    inProcessFSM.expression.add(inProcessFSM.variables.peek());
                    inProcessFSM.expression.add(new OperatorToken(token.value.substring(0, 1)));
                    inProcessFSM.expression.add(new PunctuationToken("("));
                    break;
                case ADD_CLOSE_PARENTHESIS_AND_CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER:
                    inProcessFSM.expression.add(new PunctuationToken(")"));
                    int resultPlace3 = OPCodeGenerator.calculateExpression(inProcessFSM.expression);
                    OPCodeGenerator.loadMem(resultPlace3, "00");
                    String memSelName2 = inProcessFSM.variables.pop().value;
                    OPCodeGenerator.storeMem(memSelName2, "00");
                    break;
            }
        }
    }







    public void generate(){
        StateMachine theStateMachine = new StateMachine();
        for(Token token : ClassifiedData.getInstance().tokens){
            try {
                fsm.onEvent(theStateMachine, token.getEvent().toString(), token);
            } catch (TooBusyException e) {
                e.printStackTrace();
            }
        }
        ClassifiedData.getInstance().onEndOfTokens();
    }

}

