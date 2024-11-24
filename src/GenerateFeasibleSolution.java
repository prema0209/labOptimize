
import java.util.*;


public class GenerateFeasibleSolution {

    Random r;
    int iterasi;
    double probabilitasEksplorasi;
    public void createFeasibleSolution(){

        r=new Random();
        iterasi=0;

        boolean phase=true;
        int index=0;
        int index2=0;
        int indexStuck=0;
        int best=Main.student.size()+Main.teachingAssitants.size();
        boolean adaPerubahan=false;
        int bestSementara=0;
        double penurun = 0.3;
       probabilitasEksplorasi = 1 - penurun;
        int indexStuckGlobal=0;
        int bestSolution2 =Main.student.size()+Main.teachingAssitants.size();




        while (Main.student.size()>0 || Main.teachingAssitants.size()>0){

            if(iterasi%1000==0){
               System.out.println("iterasi:"+iterasi+" best:"+best+" saat ini:"+(Main.student.size()+Main.teachingAssitants.size())+" "+indexStuckGlobal);
            }



            iterasi++;

            if((Main.student.size()+Main.teachingAssitants.size())<best){
                best=(Main.student.size()+Main.teachingAssitants.size());
            }


            if ((Main.student.size()+Main.teachingAssitants.size()) < bestSolution2) {
                bestSolution2 = (Main.student.size()+Main.teachingAssitants.size());
                indexStuckGlobal = 0;
            } else {
                indexStuckGlobal++;
            }

            if (indexStuckGlobal > 100000) {
                penurun *= 0.85;
                probabilitasEksplorasi = 1 - penurun;
                System.out.println("update prob:"+probabilitasEksplorasi);

                if (probabilitasEksplorasi > 0.991) {
                    // exploitasi=false;

                    penurun = 1;
                    probabilitasEksplorasi = 1 - penurun;

                }
                indexStuckGlobal  = 0;
                bestSolution2 = (Main.student.size()+Main.teachingAssitants.size());
            }


            if(phase){
                //System.out.println(iterasi+";"+(Main.student.size()+Main.teachingAssitants.size()));
                //System.out.println("Jumlah Exam Tidak Terjadwal Awal:"+Main.exams.size());

                double x=Math.random();


                    moveCohort();




              //  addGame(Main.exams.get(r.nextInt(Main.exams.size())),0);
                phase=false;
                Collections.shuffle(Main.student);
                Collections.shuffle(Main.teachingAssitants);
                index=0;//System.out.println("Jumlah Exam Tidak Terjadwal Akhr:"+Main.exams.size());
                index2=0;
                adaPerubahan=false;
                bestSementara=Main.student.size()+Main.teachingAssitants.size();

//                int examTerjadwal=0;
//                for(int i=0;i<Main.timeSlot.length;i++){
//                    examTerjadwal+=Main.timeSlot[i].size();
//                }

                //System.out.println("exam terjadwal:"+examTerjadwal+" exam tdk terjadwal"+Main.exams.size()+" total:"+(examTerjadwal+Main.exams.size()));
            }else{
                boolean perubahan;
                //perubahan=addGame(Main.exams.get(index),1);






                if(index<Main.student.size()) {
                    perubahan = Main.addStudent(index);

                    index++;
                    if (perubahan) adaPerubahan = true;
                }





                if(index2<Main.teachingAssitants.size()){
                    perubahan=Main.addTeachingAssitant(index2);

                    index2++;

                    if (perubahan) adaPerubahan = true;
                }










                if(index>=Main.student.size() && index2>=Main.teachingAssitants.size()){
                    index=0;
                    index2=0;

                    Collections.shuffle(Main.student);
                    Collections.shuffle(Main.teachingAssitants);

                    boolean a=false;
                    for(int i=0;i<100;i++){
                        double x=Math.random();

                        if(x<0.25){
                            a=Main.moveCohort(true);
                        }else if(x<0.5){
                            a=Main. swapCohort(true);
                        }
                        else if(x<0.75){
                            a=Main.  moveStudent(true);
                        }
                        else{
                            a=Main.  swapStudent(true);
                        }

                        if(a){
                            // System.out.println("exit");
                            break;
                        }

                    }

                    if (!a && !adaPerubahan) {
                        indexStuck = 1000;
                        //System.out.println("kenaa");
                    }

                    if (Main.student.size()+Main.teachingAssitants.size() >= bestSementara) indexStuck++;
                    else {
                        indexStuck = 0;
                        bestSementara = Main.student.size()+Main.teachingAssitants.size();

                    }


                    if(indexStuck>20){
                        phase=true;
                        indexStuck=0;
                    }
                    else{

                        adaPerubahan=false;



                    }

                }
            }
        }
    }


    public boolean moveCohort(){
        r=new Random();


        Cohort c = Main.cohort.get(r.nextInt(Main.cohort.size()));

        List<TimeSlot> ts = new ArrayList<>();
        List<Integer> numberConflict = new ArrayList<>();

        for (int i = 0; i < Main.timeSlot.size(); i++) {
            if (Main.timeSlot.get(i).cohort == null) {

                if (Main.timeSlot.get(i) == c.timeSlot) {
                    System.out.println("sama");
                    System.exit(0);
                }

                ts.add(Main.timeSlot.get(i));
                int konflik=c.tesKonfilk(Main.timeSlot.get(i).timeSlotId);
                if(c.students.size()>Main.timeSlot.get(i).room.kapasitas){
                    konflik+=c.students.size()-Main.timeSlot.get(i).room.kapasitas;
                }
                numberConflict.add(konflik);
            }
        }

        List<AbstractMap.SimpleEntry<TimeSlot, Integer>> combined = new ArrayList<>();
        for (int i = 0; i < ts.size(); i++) {
            combined.add(new AbstractMap.SimpleEntry<>(ts.get(i), numberConflict.get(i)));
        }


        Collections.sort(combined, Comparator.comparing(AbstractMap.SimpleEntry::getValue));

        ts.clear(); // Clear the original lists
        numberConflict.clear();




        TimeSlot terpilih;
        if(Math.random()<probabilitasEksplorasi){
            int xx=combined.get(0).getValue();
            int batasRandom=0;

            for(int i=0;i<combined.size();i++){
                if(xx!=combined.get(i).getValue()){
                    break;
                }else{
                    batasRandom=i;
                }
            }

            batasRandom++;
            terpilih= combined.get(r.nextInt(batasRandom)).getKey();


        }else{


             terpilih= combined.get(r.nextInt(combined.size())).getKey();
        }

        c.timeSlot.cohort = null;
        c.timeSlot = terpilih;
        terpilih.cohort = c;

        c.removeKonflik();
        c.cekKesesuaian();

        while (c.students.size()>c.timeSlot.room.kapasitas){

            Student x=c.students.get(r.nextInt(c.students.size()));
            Main.student.add(x);
            c.students.remove(x);
        }

        if(c.students.size()>c.timeSlot.room.kapasitas)System.exit(0);




        return true;

    }




}
