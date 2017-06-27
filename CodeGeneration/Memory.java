package src.CodeGeneration;

public class Memory {
    private static final int SAYEH_MEMORY_SIZE = 1024;
    private String memNames[];
    private boolean isAllocated[];
    private boolean isTemp[];
    private static Memory RAM;
    private Memory(){
        memNames = new String[SAYEH_MEMORY_SIZE];
        isAllocated = new boolean[SAYEH_MEMORY_SIZE];
        isTemp = new boolean[SAYEH_MEMORY_SIZE];
    }

    public static Memory getRAM (){
        if(RAM == null){
            RAM = new Memory();
        }
        return RAM;
    }

    public String aloc(){
        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            if(!isAllocated[i]){
                isAllocated[i] = true;
                isTemp[i] = true;
                memNames[i] = "temp" + i;
                return memNames[i];
            }
        }
        try {
            throw new Exception("Not enough memory available");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int aloc(String name){
//        if(name.matches("temp[0-9]")){
//            try {
//                throw new Exception("Wrong name format");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            String sMem = memNames[i];
            if(isAllocated[i] && sMem.equals(name)) {
                try {
                    throw new Exception("Duplicate memory name");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1;
            }
        }

        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            if(!isAllocated[i]){
                isAllocated[i] = true;
                isTemp[i] = false;
                memNames[i] = name;
                return i;
            }
        }
        try {
            throw new Exception("Not enough memory available");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void free(String name){
        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            String sMem = memNames[i];
            if(isAllocated[i] && sMem.equals(name)) {
                isAllocated[i] = false;
                memNames[i] = "";
                return;
            }
        }
        try {
            throw new Exception("Can't find this memory name");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void freeTemps() {
        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            if(isAllocated[i] && isTemp[i]){
                isAllocated[i] = false;
            }
        }
    }

    public int find(String name){
        for(int i = 0; i < SAYEH_MEMORY_SIZE; i++){
            String sMem = memNames[i];
            if(isAllocated[i] && sMem.equals(name)) {
                return i;
            }
        }
        try {
            throw new Exception("Can't find this memory name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
