package src.CodeGeneration;

import org.statefulj.fsm.FSM;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.TooBusyException;
import org.statefulj.fsm.model.Action;
import org.statefulj.fsm.model.impl.StateActionPairImpl;
import org.statefulj.fsm.model.impl.StateImpl;
import org.statefulj.fsm.model.State;
import org.statefulj.persistence.memory.MemoryPersisterImpl;
import src.CodeGeneration.FSM.StateMachine;
import src.Token.IdentifierToken;
import src.Token.OperatorToken;
import src.Token.PunctuationToken;
import src.Token.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class CodeGenerator {


    private List<State<StateMachine>> states;
    private MemoryPersisterImpl<StateMachine> persister;
    private State<StateMachine> start,
                    def, def1, def2, def3,
                    asg, asg1, asg2, advAsg1, advAsg2,
                    cond, cond1, cond2, cond3, extraCond,  cond4, cond5, elseCond, elseExtraCond,
                    while0, while1, while2, while3, extraWhile;

    private FSM fsm;

    private OPCodeGenerator opCodeGenerator;
    ArrayList<Token> inputTokens;
    public CodeGenerator(ArrayList<Token> inputTokens, ArrayList<String> outputOpcodes, String nameSpace){
        this.inputTokens = inputTokens;
        opCodeGenerator = new OPCodeGenerator(outputOpcodes);

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
        cond = new StateImpl<>("cond");
        cond1 = new StateImpl<>("cond 1");
        cond2 = new StateImpl<>("cond 2");
        cond3 = new StateImpl<>("cond 3");
        cond4 = new StateImpl<>("cond 4");
        cond5 = new StateImpl<>("cond 5");
        elseCond = new StateImpl<>("else cond");
        extraCond = new StateImpl<>("extra cond");
        elseExtraCond = new StateImpl<>("else extra cond");
        while0 = new StateImpl<>("while 0");
        while1 = new StateImpl<>("while 1");
        while2 = new StateImpl<>("while 2");
        while3 = new StateImpl<>("while 3");
        extraWhile = new StateImpl<>("extra while");

        addStartStateTransitions(start, (stateful, event, args) -> {
                    stateful.expression.clear();
                    stateful.variables.clear();
                    stateful.nestedTokens.clear();
                    stateful.whileExpressionOpcodes.clear();
                    stateful.braceNumber = 0;
                    stateful.parenthesisNumber = 0;
                },
                new theAction<>(StateMachine.Action.ADD_IDENTIFIER_TO_VAR_STACK));

        def.addTransition(StateMachine.Event.IDENTIFIER.toString(), def1,
                new theAction<>(StateMachine.Action.ADD_IDENTIFIER_TO_VAR_STACK));

        def1.addTransition(StateMachine.Event.COMMA.toString(), def);
        def1.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.DECLARE_IDENTIFIERS));
        def1.addTransition(StateMachine.Event.ASSIGN_OPERATOR.toString(), def2);

        def2.addTransition(StateMachine.Event.VALUE.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        def2.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        def3.addTransition(StateMachine.Event.VALUE.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        def3.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), def3,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        def3.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), def3,
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
        asg1.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        asg2.addTransition(StateMachine.Event.VALUE.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), asg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        asg2.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER));

        advAsg1.addTransition(StateMachine.Event.VALUE.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg1.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));

        advAsg2.addTransition(StateMachine.Event.VALUE.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), advAsg2,
                new theAction<>(StateMachine.Action.ADD_EXPRESSION_TO_EXP_STACK));
        advAsg2.addTransition(StateMachine.Event.SEMICOLON.toString(), start,
                new theAction<>(StateMachine.Action.ADD_CLOSE_PARENTHESIS_AND_CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER));

        cond.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), cond1, (stateful, event, args) ->
                stateful.parenthesisNumber++);
        cond1.addTransition(StateMachine.Event.VALUE.toString(), cond1, (stateful, event, args) -> stateful.expression.add((Token)args[0]));
        cond1.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), cond1, (stateful, event, args) -> {
            stateful.parenthesisNumber++;
            stateful.expression.add((Token)args[0]);
        });
        cond1.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), (stateful, event, args) -> {
                    stateful.parenthesisNumber--;
                    if(stateful.parenthesisNumber == 0){
                        return new StateActionPairImpl<>(cond2, null);
                    } else {
                        stateful.expression.add((Token)args[0]);
                        return new StateActionPairImpl<>(cond1, null);
                    }
                });

        cond2.addTransition(StateMachine.Event.OPEN_BRACE.toString(), cond3, (stateful, event, args) -> {
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
            stateful.braceNumber++;
        });

        addNestedTransitions(cond3, cond3);

        cond3.addTransition(StateMachine.Event.OPEN_BRACE.toString(), cond3, (stateful, event, args) -> {
            stateful.braceNumber++;
            stateful.nestedTokens.add((Token)args[0]);
        });
        cond3.addTransition(StateMachine.Event.CLOSE_BRACE.toString(), (stateful, event, args) -> {
            stateful.braceNumber--;
            if(stateful.braceNumber == 0){
                return new StateActionPairImpl<>(cond4, null);
            } else {
                stateful.nestedTokens.add((Token)args[0]);
                return new StateActionPairImpl<>(cond3, null);
            }
        });

        addEveryTransitionsExceptBracesAndSemicolonAndCalcExp(cond2, extraCond);
        cond2.addTransition(StateMachine.Event.SEMICOLON.toString(), cond4); // if is empty

        addEveryTransitionsExceptBracesAndSemicolon(extraCond, extraCond);
        extraCond.addTransition(StateMachine.Event.SEMICOLON.toString(), cond4);

        cond4.addTransition(StateMachine.Event.ELSE.toString(), cond5, (stateful, event, args) -> {
            opCodeGenerator.loadNum(0, "01");
            outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP, "00", "01"));
            ArrayList<String> nestedRes = new ArrayList<>();
            (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
            stateful.nestedTokens.clear();
            outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 2/*jump ins(for <each>), after instructions*/, 8)));
            outputOpcodes.addAll(nestedRes);
        });

        addStartStateTransitions(cond4, (stateful, event, args) ->
                {
                    opCodeGenerator.loadNum(0, "01");
                    outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP, "00", "01"));
                    ArrayList<String> nestedRes = new ArrayList<>();
                    (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
                    stateful.nestedTokens.clear();
                    outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 1/*after instructions*/, 8)));
                    outputOpcodes.addAll(nestedRes);
                },
                (stateful, event, args) -> {
                    opCodeGenerator.loadNum(0, "01");
                    outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP, "00", "01"));
                    ArrayList<String> nestedRes = new ArrayList<>();
                    (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
                    stateful.nestedTokens.clear();
                    outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 1/*after instructions*/, 8)));
                    outputOpcodes.addAll(nestedRes);
                    // ----
                    stateful.variables.add((IdentifierToken) args[0]);
                }
        );

        cond5.addTransition(StateMachine.Event.OPEN_BRACE.toString(), elseCond, (stateful, event, args) -> stateful.braceNumber++);

        addNestedTransitions(elseCond, elseCond);
        elseCond.addTransition(StateMachine.Event.OPEN_BRACE.toString(), elseCond, (stateful, event, args) -> {
            stateful.braceNumber++;
            stateful.nestedTokens.add((Token)args[0]);
        });
        elseCond.addTransition(StateMachine.Event.CLOSE_BRACE.toString(), (stateful, event, args) -> {
            stateful.braceNumber--;
            if(stateful.braceNumber == 0){
                ArrayList<String> nestedRes = new ArrayList<>();
                (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
                stateful.nestedTokens.clear();
                outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 1/*after instructions*/, 8)));
                outputOpcodes.addAll(nestedRes);
                return new StateActionPairImpl<>(start, null);
            } else {
                stateful.nestedTokens.add((Token)args[0]);
                return new StateActionPairImpl<>(elseCond, null);
            }
        });

        addEveryTransitionsExceptBracesAndSemicolon(cond5, elseExtraCond);
        cond5.addTransition(StateMachine.Event.SEMICOLON.toString(), start, (stateful, event, args) ->
            stateful.nestedTokens.clear()
        ); // else is empty

        addEveryTransitionsExceptBracesAndSemicolon(elseExtraCond, elseExtraCond);
        elseExtraCond.addTransition(StateMachine.Event.SEMICOLON.toString(), start, (stateful, event, args) -> {
            ArrayList<String> nestedRes = new ArrayList<>();
            (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
            stateful.nestedTokens.clear();
            outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 1/*after instructions*/, 8)));
            outputOpcodes.addAll(nestedRes);
        });

        while0.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), while1, (stateful, event, args) -> {
                stateful.parenthesisNumber++;
                stateful.whileExpressionOpcodes.clear();
        });

        while1.addTransition(StateMachine.Event.VALUE.toString(), while1, (stateful, event, args) ->
                                stateful.expression.add((Token)args[0]));
        while1.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), while1, (stateful, event, args) -> {
            stateful.parenthesisNumber++;
            stateful.expression.add((Token)args[0]);
        });
        while1.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), (stateful, event, args) -> {
            stateful.parenthesisNumber--;
            if(stateful.parenthesisNumber == 0){
                return new StateActionPairImpl<>(while2, null);

            } else {
                stateful.expression.add((Token)args[0]);
                return new StateActionPairImpl<>(while1, null);
            }
        });

        while2.addTransition(StateMachine.Event.SEMICOLON.toString(), start/*,
                (stateful, event, args) -> stateful.whileExpression.clear()*/);
        while2.addTransition(StateMachine.Event.OPEN_BRACE.toString(), while3, (stateful, event, args) -> {
            OPCodeGenerator expressionOpcodeGenerator = new OPCodeGenerator(stateful.whileExpressionOpcodes);
            int resultPlace = expressionOpcodeGenerator.calculateExpression(stateful.expression);
            expressionOpcodeGenerator.loadMem(resultPlace, "00");
            stateful.braceNumber++;
        });

        addNestedTransitions(while3, while3);

        while3.addTransition(StateMachine.Event.OPEN_BRACE.toString(), while3, (stateful, event, args) -> {
            stateful.braceNumber++;
            stateful.nestedTokens.add((Token)args[0]);
        });
        while3.addTransition(StateMachine.Event.CLOSE_BRACE.toString(), (stateful, event, args) -> {
            stateful.braceNumber--;
            if(stateful.braceNumber == 0){
                ArrayList<String> nestedRes = new ArrayList<>();
                (new CodeGenerator(stateful.nestedTokens, nestedRes, fsm.getName() + "_nesting")).generate();
                stateful.nestedTokens.clear();

                outputOpcodes.addAll(stateful.whileExpressionOpcodes);
                opCodeGenerator.loadNum(0, "01");
                outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP, "00", "01"));
                outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, OPCodeGenerator.toBin(nestedRes.size() + 2/*jump ins, nested inss*/, 8)));
                outputOpcodes.addAll(nestedRes);
                outputOpcodes.add(OPCode.getOpcode(OPCode.OPCODE_16_I.JPR,
                        OPCodeGenerator.toBin(-(nestedRes.size() + 4 + stateful.whileExpressionOpcodes.size()), 8)));

                return new StateActionPairImpl<>(start, null);
            } else {
                stateful.nestedTokens.add((Token)args[0]);
                return new StateActionPairImpl<>(while3, null);
            }
        });

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
        states.add(cond);
        states.add(cond1);
        states.add(cond2);
        states.add(cond3);
        states.add(cond4);
        states.add(cond5);
        states.add(elseCond);
        states.add(extraCond);
        states.add(elseExtraCond);
        states.add(while0);
        states.add(while1);
        states.add(while2);
        states.add(while3);
        states.add(extraWhile);

        persister = new MemoryPersisterImpl<>(states, start);
        fsm = new FSM(nameSpace, persister);
        System.out.printf("");
    }



    private void addStartStateTransitions(State<StateMachine> state, Action<StateMachine> defaultAction,
                                          Action<StateMachine> defaultPlusIdentifyingVarAction){
        state.addTransition(StateMachine.Event.KEYWORD_VAR.toString(), def, defaultAction);
        state.addTransition(StateMachine.Event.IDENTIFIER.toString(), asg, defaultPlusIdentifyingVarAction);
        state.addTransition(StateMachine.Event.IF.toString(), cond, defaultAction);
        state.addTransition(StateMachine.Event.WHILE.toString(), while0, defaultAction);
        state.addTransition(StateMachine.Event.SEMICOLON.toString(), state);
    }

    private void addNestedTransitions(State<StateMachine> state, State<StateMachine> targetState){ // Any token except Braces
        addEveryTransitionsExceptBracesAndSemicolon(state, targetState);
        state.addTransition(StateMachine.Event.SEMICOLON.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0]));
    }

    private void addEveryTransitionsExceptBracesAndSemicolon(State<StateMachine> state, State<StateMachine> targetState){
        state.addTransition(StateMachine.Event.KEYWORD_VAR.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.ASSIGN_OPERATOR.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.ADVANCED_ASSIGN_OPERATOR.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.IDENTIFIER.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.VALUE.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.COMMA.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.IF.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.ELSE.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
        state.addTransition(StateMachine.Event.WHILE.toString(), targetState, (stateful, event, args) ->
                stateful.nestedTokens.add((Token)args[0])
        );
    }

    private void addEveryTransitionsExceptBracesAndSemicolonAndCalcExp(State<StateMachine> state, State<StateMachine> targetState) {
        state.addTransition(StateMachine.Event.KEYWORD_VAR.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.ASSIGN_OPERATOR.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.ADVANCED_ASSIGN_OPERATOR.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.COMPUTABLE_OPERATOR.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.IDENTIFIER.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.VALUE.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.COMMA.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.OPEN_PARENTHESIS.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.CLOSE_PARENTHESIS.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.IF.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.ELSE.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
        state.addTransition(StateMachine.Event.WHILE.toString(), targetState, (stateful, event, args) -> {
            stateful.nestedTokens.add((Token) args[0]);
            int resultPlace = opCodeGenerator.calculateExpression(stateful.expression);
            opCodeGenerator.loadMem(resultPlace, "00");
        });
    }

    private static class theAction<T> implements Action<T> {

        StateMachine.Action action;
        private theAction(StateMachine.Action action){
            this.action = action;
        }


        @Override
        public void execute(T stateful, String event, Object... args) throws RetryException {
            Token token = (Token) args[0];
            OPCodeGenerator opCodeGenerator = (OPCodeGenerator) args[1];
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
                    int resultPlace = opCodeGenerator.calculateExpression(inProcessFSM.expression);
                    opCodeGenerator.loadMem(resultPlace, "00");
                    while(!inProcessFSM.variables.isEmpty()){
                        String memSelName = inProcessFSM.variables.pop().value;
                        Memory.getRAM().aloc(memSelName);
                        opCodeGenerator.storeMem(memSelName, "00", "01");
                    }
                    break;
                case CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER:
                    int resultPlace2 = opCodeGenerator.calculateExpression(inProcessFSM.expression);
                    opCodeGenerator.loadMem(resultPlace2, "00");
                    String memSelName = inProcessFSM.variables.pop().value;
                    opCodeGenerator.storeMem(memSelName, "00", "01");
                    break;
                case ADD_IDENTIFIER_TO_EXP_STACK:
                    inProcessFSM.expression.add(inProcessFSM.variables.peek());
                    inProcessFSM.expression.add(new OperatorToken(token.value.substring(0, 1)));
                    inProcessFSM.expression.add(new PunctuationToken("("));
                    break;
                case ADD_CLOSE_PARENTHESIS_AND_CALCULATE_EXPRESSION_AND_SET_VALUE_TO_IDENTIFIER:
                    inProcessFSM.expression.add(new PunctuationToken(")"));
                    int resultPlace3 = opCodeGenerator.calculateExpression(inProcessFSM.expression);
                    opCodeGenerator.loadMem(resultPlace3, "00");
                    String memSelName2 = inProcessFSM.variables.pop().value;
                    opCodeGenerator.storeMem(memSelName2, "00", "01");
                    break;
            }
        }
    }







    public void generate(){
        StateMachine theStateMachine = new StateMachine();
        for(Token token : inputTokens){
            try {
                fsm.onEvent(theStateMachine, token.getEvent().toString(), token, opCodeGenerator);
            } catch (TooBusyException e) {
                e.printStackTrace();
            }
        }
    }

}

