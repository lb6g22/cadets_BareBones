package week2;

public class bareVar {

    //Attributes
    private int value = 0;
    private String name;

    //Constructor
    public bareVar(String myName){
        name = myName;
    }

    public int getValue() {
        return value;
    }

    public String getName(){
        return name;
    }

    public void incr(){
        value++;
    }

    public void decr(){
        value--;
    }

    public void zero(){
        value = 0;
    }
}
