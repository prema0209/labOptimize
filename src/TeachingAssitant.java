import java.util.Arrays;

public class TeachingAssitant {

    String id;
    String nama;
    String codeboard;

    String initial;
    boolean[] avail;

    public TeachingAssitant(String i, String n, String k, String in, boolean[] a){
        id=i;
        nama=n;
        codeboard=k;
        avail=a;
        initial=in;
    }

    public void print(){
        System.out.println(id+" "+nama+" "+codeboard+""+initial+" "+ Arrays.toString(avail));
    }

    public boolean cekAvail(int x){
        return avail[x];
    }
}
