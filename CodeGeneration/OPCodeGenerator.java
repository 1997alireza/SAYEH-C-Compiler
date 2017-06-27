package src.CodeGeneration;

import src.ClassifiedData;
import src.Token.*;

import java.util.ArrayList;
import java.util.Stack;

public class OPCodeGenerator {
    public static void loadNum(int num, String regAdr){
        String b = toBin(num, 16);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_DI.MIL, regAdr, b.substring(8, 16)));
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_DI.MIH, regAdr, b.substring(0, 8)));
    }

    public static void loadMem(String name, String regAdr){ // mem -> reg
        int memSelPlace = Memory.getRAM().find(name);
        loadNum(memSelPlace, regAdr);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.LDA, regAdr, regAdr));
    }

    public static void loadMem(int memSelPlace, String regAdr){ // mem -> reg
        loadNum(memSelPlace, regAdr);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.LDA, regAdr, regAdr));
    }

    public static void storeMem(String name, String regAdr){ // reg -> mem
        int memSelPlace = Memory.getRAM().find(name);
        loadNum(memSelPlace, regAdr);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.STA, regAdr, regAdr));
    }

    public static void loadOperand(Token token, String regAdr){ // ... -> reg
        switch (token.type){
            case Identifier:
                loadMem(token.value, regAdr);
                break;
            case Number:
                loadNum(((NumberToken)token).intValue, regAdr);
        }
    }

    private static String toBin(int num, int length){
        boolean isNegative = num < 0;
        String b = Integer.toBinaryString(num);
        if(isNegative) {
            b = b.substring(b.length()-length, b.length());
        }
        if(b.length() > length){
            try {
                throw new Exception("The number is bigger than binary space");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String signBit = (isNegative) ? "1" : "0";
        while(b.length() < length)
            b = signBit + b;

        return b;
    }

    /**\
     *
     * @param expression
     * @return place of result in memory
     */
    public static int calculateExpression(ArrayList<Token> expression){
        Stack<Token> operands = new Stack<>(); // NumberToken or IdentifierToken
        Stack<OperatorToken> operators = new Stack<>();

        boolean lastTokenIsIden = false;
        for (Token token : expression){
            switch (token.type){
                case Operator:
                    if(getPriority(token.value) > getTopPriority(operators)){
                        operators.add((OperatorToken) token);
                    }
                    else {
                        while (getPriority(token.value) > getTopPriority(operators)){
                            OperatorToken op = operators.pop();

                            boolean operationWithOneOperand = true;
                            Token opToken00 = operands.pop();
                            loadOperand(opToken00, "00");

                            switch (op.value) {
                                case "!":
                                    notRegister("00", "01");
                                    break;
                                case "++":
                                    if(lastTokenIsIden){ // ++i(00)
                                        loadNum(1, "01");
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ADD,
                                                "00", "01"));
                                        storeMem(opToken00.value, "00");

                                        operands.add(opToken00);
                                    }
                                    else { // i(00)++
                                        String tempMem = Memory.getRAM().aloc();
                                        storeMem(tempMem, "00");
                                        operands.add(new IdentifierToken(tempMem, -1, -1));

                                        loadNum(1, "01");
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ADD,
                                                "00", "01"));
                                        storeMem(opToken00.value, "00");
                                    }

                                    continue;
                                case "--":
                                    if(lastTokenIsIden){ // --i(00)
                                        loadNum(-1, "01");
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ADD,
                                                "00", "01"));
                                        storeMem(opToken00.value, "00");

                                        operands.add(opToken00);
                                    }
                                    else { // i(00)--
                                        String tempMem = Memory.getRAM().aloc();
                                        storeMem(tempMem, "00");
                                        operands.add(new IdentifierToken(tempMem, -1, -1));

                                        loadNum(-1, "01");
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ADD,
                                                "00", "01"));
                                        storeMem(opToken00.value, "00");
                                    }

                                    continue;
                                default:
                                    operationWithOneOperand = false;
                            }

                            if(!operationWithOneOperand) {
                                loadOperand(operands.pop(), "01");
                                switch (op.value) {
                                    case "&&":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.AND,
                                                "00", "01"));
                                        break;
                                    case "||":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ORR,
                                                "00", "01"));
                                        break;
                                    case "==":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.SUB,
                                                "00", "01"));
                                        notRegister("00", "01");
                                        break;
                                    case "!=":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.SUB,
                                                "00", "01"));
                                        break;
                                    case ">": // reg(01) > reg(00)
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP,
                                                "00", "01"));
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.BRC,
                                                "00000100"));
                                        loadNum(0, "00"); // reg(01) <= reg(00)
                                        jump(3);
                                        loadNum(1, "00"); // reg(01) > reg(00)
                                        break;
                                    case "<": // reg(01) < reg(00)
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP,
                                                "01", "00"));
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.BRC,
                                                "00000100"));
                                        loadNum(0, "00"); // reg(01) >= reg(00)
                                        jump(3);
                                        loadNum(1, "00"); // reg(01) < reg(00)
                                        break;
                                    case ">=": // reg(01) >= reg(00)
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP,
                                                "01", "00"));
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.BRC,
                                                "00000100"));
                                        loadNum(1, "00"); // reg(01) >= reg(00)
                                        jump(3);
                                        loadNum(0, "00"); // reg(01) < reg(00)
                                        break;
                                    case "<=": // reg(01) <= reg(00)
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP,
                                                "00", "01"));
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.BRC,
                                                "00000100"));
                                        loadNum(1, "00"); // reg(01) <= reg(00)
                                        jump(3);
                                        loadNum(0, "00"); // reg(01) > reg(00)
                                        break;
                                    case "+":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.ADD,
                                                "00", "01"));
                                        break;
                                    case "-": // D - S
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.SUB,
                                                "00", "01"));
                                        break;
                                    case "*":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.MUL,
                                                "00", "01"));
                                        break;
                                    case "/":
                                        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.DIV,
                                                "00", "01"));
                                        break;
                                }
                            }

                            String tempMem = Memory.getRAM().aloc();
                            storeMem(tempMem, "00");
                            operands.add(new IdentifierToken(tempMem, -1, -1));
                        }
                    }

                    lastTokenIsIden = false;
                    break;
                case Number:
                case Character:
                case Keyword:
                    operands.add(getValue(token));
                    lastTokenIsIden = false;
                    break;
                case Identifier:
                    operands.add(token);
                    lastTokenIsIden = true;
            }
        }

        return -1   ;
    }

    private static void notRegister(String regAdr, String zeroKeeperRegAdr){ // true -> false, false -> true : false = 0
        if(regAdr.length() != 2 || zeroKeeperRegAdr.length() != 2){
            try {
                throw new Exception("Wrong register address");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(regAdr.equals(zeroKeeperRegAdr)){
            try {
                throw new Exception("Same registers addresses");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        loadNum(0, zeroKeeperRegAdr);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_8_DS.CMP, regAdr, zeroKeeperRegAdr));
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.BRZ, "00000100"));
        loadNum(0, regAdr); // value of regAdr is true, set it to false
        jump(3);
        loadNum(1, regAdr); // value of regAdr is false, set it to true
    }

    private static void jump(int numOfLines){
        String I = toBin(numOfLines, 8);
        ClassifiedData.getInstance().addOpcode(OPCode.getOpcode(OPCode.OPCODE_16_I.JPR, I));
    }

    private static int getTopPriority(Stack<OperatorToken> operators){
        if(operators.isEmpty()){
            return -1;
        }

        return getPriority(operators.peek().value);
    }

    private static int getPriority(String operation){
        switch (operation){
            case "/":
                return 5;
            case "*":
                return 5;
            case "-":
                return 4;
            case "+":
                return 4;
            case "!":
                return 2;
            case "||":
                return 0;
            case "&&":
                return 1;
            case "<=":
                return 3;
            case ">=":
                return 3;
            case "<":
                return 3;
            case ">":
                return 3;
            case "==":
                return 3;
            case "!=":
                return 3;
            case "++":
                return 6;
            case "--":
                return 6;
        }

        try {
            throw new Exception("Wrong Operation");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static NumberToken getValue(Token token){
        switch (token.type) {
            case Number:
                return (NumberToken)token;
            case Character:
                return new NumberToken(((CharacterToken)token).character, -1, -1);
            case Keyword:
                if(token.value.equals("true")) // true -> anything not zero
                    return new NumberToken(1, -1, -1);
                if(token.value.equals("false")) // false -> 0
                    return new NumberToken(0, -1, -1);
        }

        try {
            throw new Exception("Wrong token. It's not a value");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
