package src.CodeGenerationAndSyntaxChecker;

public class OPCode {
    public enum OPCODE_8 {
        // opcode : 8bit
        
        NOP("00000000"),
        HLT("00000001"),
        SZF("00000010"),
        CZF("00000011"),
        SCF("00000100"),
        CCF("00000101"),
        CWP("00000110");

        private String opcode;
        OPCODE_8(String opcode){
            this.opcode = opcode;
        }
        public String toString(){
            return this.opcode;
        }
    }

    public enum OPCODE_16_I {
        // opcode-I : 16bit

        JPR("00000111"),
        BRZ("00001000"),
        BRC("00001001"),
        AWP("00001010");

        private String opcode;
        OPCODE_16_I(String opcode){
            this.opcode = opcode;
        }
        public String toString(){
            return this.opcode;
        }
    }

    public enum OPCODE_8_DS {
        // opcode-D-S : 8bit
        MVR("0001"),
        LDA("0010"),
        STA("0011"),
        TCM("0100"),
        RND("0101"),
        AND("0110"),
        ORR("0111"),
        NOT("1000"),
        SHL("1001"),
        SHR("1010"),
        ADD("1011"),
        SUB("1100"),
        MUL("1101"),
        DIV("1101"), // SAYEH hasn't this instruction yet :(
        CMP("1110");

        private String opcode;
        OPCODE_8_DS(String opcode){
            this.opcode = opcode;
        }
        public String toString(){
            return this.opcode;
        }
    }

    public enum OPCODE_16_DI {
        // 1111-D-opcode-I : 16bit
        MIL("00"),
        MIH("01"),
        SPC("10"),
        JPA("11");

        private String opcode;
        OPCODE_16_DI(String opcode){
            this.opcode = opcode;
        }
        public String toString(){
            return this.opcode;
        }
    }

    public static String getOpcode(OPCODE_8 opcode){
        return opcode.toString();
    }

    public static String getOpcode(OPCODE_16_I opcode, String I){
        if(I.length() != 8){
            try {
                throw new Exception("Wrong immediate size");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return opcode.toString() + I;
    }

    public static String getOpcode(OPCODE_8_DS opcode, String D, String S){
        if(D.length() != 2 || S.length() != 2){
            try {
                throw new Exception("Wrong register address");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return opcode.toString() + D + S;
    }

    public static String getOpcode(OPCODE_16_DI opcode, String D, String I){
        if(D.length() != 2){
            try {
                throw new Exception("Wrong register address");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(I.length() != 8){
            try {
                throw new Exception("Wrong immediate size");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "1111" + D + opcode.toString() + I;
    }

}
