import java.util.Arrays;

public class Student {


    String id;
    String nama;
    String kelas;
    boolean[] avail;

    public Student(String i, String n, String k, boolean[] a){
        id=i;
        nama=n;
        kelas=k;
        avail=a;
    }

    public boolean cekAvail(int x){
        return avail[x];
    }

    public void praproses(){
        for(int i=0;i<avail.length;i++){
            if(avail[i]){
                boolean t=false;
                for(int j=0;j<Main.timeSlot.size();j++){
                    if(Main.timeSlot.get(j).timeSlotId==i){
                        t=true;
                        break;
                    }
                }

                if(!t){
                    avail[i]=false;
                    //System.out.println("kena eliminasi");
                }
            }
        }

    }

    public boolean cekALlFalse(){
        boolean x=false;

        for(int i=0;i<avail.length;i++){
            if(avail[i])x=true;
        }

        return !x;

    }

    public int cekOnlyOne(){
        int ii=0;
        int index=-1;

        for(int i=0;i<avail.length;i++){
            if(avail[i])ii++;
            index=i;
        }

       if(ii==1){
           return  index;
       }

        return -1;
    }


    public void print(){
        System.out.println(id+" "+nama+" "+kelas+" "+ Arrays.toString(avail));
    }

}
