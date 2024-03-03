import java.util.Arrays;

public class Room {
    String id;
    String nama;
    int kapasitas;
    boolean[] avail;

    public Room(String i, String n, int k, boolean[] a){
        id=i;
        nama=n;
        kapasitas=k;
        avail=a;


    }

    public void print(){
        System.out.println(id+" "+nama+" "+kapasitas+" "+ Arrays.toString(avail));
    }


}
