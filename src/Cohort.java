import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cohort implements Comparable<Cohort>{

    public int compareTo(Cohort other) {
        return Integer.compare(this.id, other.id);
    }

    int id;
    List<Student> students;
    List<TeachingAssitant> teachingAssitants;

    TimeSlot timeSlot;


    List<Student> studentsRollback;
    List<TeachingAssitant> teachingAssitantsRollback;

    TimeSlot timeSlotRollback;

    List<Student> studentsBest;
    List<TeachingAssitant> teachingAssitantsBest;

    TimeSlot timeSlotBest;

    public Cohort(int i){
        id=i;
        students=new ArrayList<>();
        teachingAssitants=new ArrayList<>();
    }

    public void printJadwal(){
        System.out.println("Cohor:"+id+" "+timeSlot.timeSlotId+" "+timeSlot.room.nama);
    }

    public void setRollback(){
        timeSlotRollback=timeSlot;
        studentsRollback=new ArrayList<>();
        teachingAssitantsRollback=new ArrayList<>();

        for(int i=0;i<students.size();i++){
            studentsRollback.add(students.get(i));
        }
        for(int i=0;i<teachingAssitants.size();i++){
            teachingAssitantsRollback.add(teachingAssitants.get(i));
        }

    }
    public void useRollback(){
        timeSlot=timeSlotRollback;
        timeSlot.cohort=this;

        students=new ArrayList<>();
        teachingAssitants=new ArrayList<>();

        for(int i=0;i<studentsRollback.size();i++){
            students.add(studentsRollback.get(i));
        }
        for(int i=0;i<teachingAssitantsRollback.size();i++){
            teachingAssitants.add(teachingAssitantsRollback.get(i));
        }

    }

    public void setBest(){
        timeSlotBest=timeSlot;



        studentsBest=new ArrayList<>();
        teachingAssitantsBest=new ArrayList<>();

        for(int i=0;i<students.size();i++){
            studentsBest.add(students.get(i));
        }
        for(int i=0;i<teachingAssitants.size();i++){
            teachingAssitantsBest.add(teachingAssitants.get(i));
        }

    }

    public void useBest(){
        timeSlot=timeSlotBest;
        timeSlot.cohort=this;

        students=new ArrayList<>();
        teachingAssitants=new ArrayList<>();

        for(int i=0;i<studentsBest.size();i++){
            students.add(studentsBest.get(i));
        }
        for(int i=0;i<teachingAssitantsBest.size();i++){
            teachingAssitants.add(teachingAssitantsBest.get(i));
        }

    }


    public int tesKonfilk(int x){
        int jumlahKonflik=0;

        for(Student student : students) {
            if(!student.cekAvail(x)){
                jumlahKonflik++;
            }
        }

        for(TeachingAssitant teachingAssitant: teachingAssitants){
            if(!teachingAssitant.cekAvail(x)){
                jumlahKonflik++;
            }
        }

        return jumlahKonflik;

    }

    public void removeKonflik(){
        List<Student> remove=new ArrayList<>();
        int x=timeSlot.timeSlotId;

        for(Student student : students) {
            if(!student.cekAvail(x)){
                remove.add(student);
            }
        }

        for(int i=0;i<remove.size();i++){
            Student xx=remove.get(i);
            students.remove(xx);
//            System.out.println("konflik dengan slot:"+x);
            Main.student.add(xx);
        }


        List<TeachingAssitant> remove2=new ArrayList<>();
        for(TeachingAssitant teachingAssitant:teachingAssitants){
            if(!teachingAssitant.cekAvail(x)){
                remove2.add(teachingAssitant);
            }
        }

        for(int i=0;i<remove2.size();i++){
            TeachingAssitant tt=remove2.get(i);
            teachingAssitants.remove(tt);
            Main.teachingAssitants.add(tt);
        }

    }

    public void cekKesesuaian(){
//        System.out.println("timeslot saat ini:"+timeSlot.timeSlotId);
        if(tesKonfilk(timeSlot.timeSlotId)>0){
            System.out.println("salah");
            System.exit(0);
        }
    }

    public void printHasil() throws IOException {

        Main.myWriter.write("\n");
        //System.out.println("\n");

        Main.myWriter.write("Cohort "+(id+1)+"\n");
        Main.myWriter.write("room:"+timeSlot.room.id+" "+timeSlot.room.nama+"\n");
//        System.out.println("Cohort "+(id+1));
//        System.out.println("room:"+timeSlot.room.id+" "+timeSlot.room.nama);

        String hari="";

        switch (timeSlot.timeSlotId/4){
            case 0:
                hari="Senin";
                break;
            case 1:
                hari="Selasa";
                break;
            case 2:
                hari="Rabu";
                break;
            case 3:
                hari="Kamis";
                break;
            case 4:
                hari="Jumat";
                break;
        }

        Main.myWriter.write("Waktu:"+hari+" sesi "+(timeSlot.timeSlotId%4+1)+"\n\n");
       // System.out.println("Waktu:"+hari+" sesi "+(timeSlot.timeSlotId%4+1));


        //System.out.println("\n");

        Main.myWriter.write("TeachingAssistant:"+"\n");
        //System.out.println("TeachingAssistant:");
        for(int i=0;i<teachingAssitants.size();i++){
            //System.out.println(teachingAssitants.get(i).nama);
            Main.myWriter.write(teachingAssitants.get(i).nama+"\n");
        }
        Main.myWriter.write("\n");
        Main.myWriter.write("Number of Student:"+students.size()+"/"+ timeSlot.room.kapasitas+"\n");
        Main.myWriter.write("Student:\n");
        Main.myWriter.write("NRP;Nama;Kelas\n");
//        System.out.println("\n");
//        System.out.println("Number of Student:"+students.size()+"/"+ timeSlot.room.kapasitas);
//        System.out.println("Student:");
//        System.out.println("NRP;Kelas;Nama");
        for(int i=0;i<students.size();i++){
            Main.myWriter.write(students.get(i).id+";"+students.get(i).nama+";"+students.get(i).kelas+"\n");
           // System.out.println(students.get(i).id+";"+students.get(i).kelas+";"+students.get(i).nama);
        }
        Main.myWriter.write("\n");
        //System.out.println("\n");
    }

}
