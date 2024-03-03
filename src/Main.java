import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;


public class Main {


    public static List<Room> room;
    public static List<Student> student;
    public static List<TeachingAssitant> teachingAssitants;
    public static List<TimeSlot> timeSlot;

    public static List<Cohort> cohort;
    public static Random r;


    public static FileWriter myWriter;
    public static String path;
    public static double probabilitasEksplorasi;

    public static int iterasi;

    public static int jumlahStudent;

    public static void main(String[] args) throws InterruptedException, IOException {

        Scanner sc = new Scanner(System.in);
        long startTime = System.currentTimeMillis();
        r = new Random();





        GetDataset input = new GetDataset();

        path = "/Users/igustiagungpremananda/IdeaProjects/LabTimetabling/";
        path= "C:/Users/igust/IdeaProjects/LabOptimize/";

        System.out.println("dataset ke?");



        int data = sc.nextInt();


        System.out.println("percobaan ke?");



        int ver = sc.nextInt();

        int penalty=1000;

        int bestPenalty=penalty;



            room = new ArrayList<>();
            student = new ArrayList<>();
            teachingAssitants = new ArrayList<>();

            input.readRoom(path + "Dataset/" + data + "/Room Timeslot.csv");
            input.readStudent(path + "Dataset/" + data + "/Student Timeslot.csv");
            input.readTeachingAssistant(path + "Dataset/" + data + "/Teaching Assistant Timeslot.csv");
            //input.readName(path + "Dataset/" + data + "/cariKelas.csv");



           // System.exit(0);



            for (int i = 0; i < student.size(); i++) {
                if (student.get(i).cekALlFalse()) {
                    //System.out.println( student.get(i).nama);
                    student.remove(student.get(i));

                    i--;
                }
            }

            jumlahStudent = student.size();

           // System.exit(0);

            timeSlot = new ArrayList<>();

            for (int i = 0; i < room.size(); i++) {
               // System.out.println(room.get(i).nama + " " + Arrays.toString(room.get(i).avail));
                for (int j = 0; j < room.get(i).avail.length; j++) {
                    if (room.get(i).avail[j]) {
                        timeSlot.add(new TimeSlot(j, room.get(i)));
                    }
                }
            }


//            for (int i = 0; i < timeSlot.size(); i++) {
//                System.out.println(timeSlot.get(i).room.nama + " " + timeSlot.get(i).timeSlotId);
//            }


            Collections.shuffle(timeSlot);


            cohort = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                cohort.add(new Cohort(i));
                while (true) {
                    int x = r.nextInt(timeSlot.size());

                    if (timeSlot.get(x).cohort == null) {
                        timeSlot.get(x).cohort = cohort.get(i);
                        cohort.get(i).timeSlot = timeSlot.get(x);
                        break;
                    }
                }
            }


            for (int i = 0; i < cohort.size(); i++) {
                //cohort.get(i).printJadwal();
            }


           // System.out.println("jumlah murid:" + jumlahStudent);


            prarposesData();


            // System.exit(0);








//
//        if (bestPenalty > penalty) {
//            System.out.println("penalty:"+penalty+" best:"+bestPenalty);
//            bestPenalty=penalty;
//            //printHasil();
//        }






           // findFeasible();

           feasibleOptimize(startTime);

           int cekStudent=0;
           for(int i=0;i<cohort.size();i++) {
              cohort.get(i).useBest();
              cekStudent+=cohort.get(i).students.size();
           }

           if(jumlahStudent!=cekStudent)System.exit(0);
           student=new ArrayList<>();
           teachingAssitants=new ArrayList<>();


            penalty = calculateFitness(jumlahStudent);


           System.out.println("penalty akhir:"+penalty);




        long endTime = System.currentTimeMillis();




        double runtime = (double) (endTime - startTime) / (1000);

        System.out.println("Elapsed Time in seconds: " + runtime);




        Collections.sort(cohort);

        printHasil(data, penalty, runtime, ver);


        //System.out.println(student.size());

    }

    public static void feasibleOptimize(long startTime){
        long duration = 72000;





        boolean phase=true;

        int index = 0;
        int indexTeachingAssistanse = 0;
        int indexStuck = 0;

        boolean adaPerubahan = false;
        int bestSementara = 0;
        double penurun = 0.3;
        probabilitasEksplorasi = 1 - penurun;

        int jumlahStudent=student.size();


        int penalty=calculateFitness(jumlahStudent);


        int bestPenalty=penalty;

        int iteasi=1;

        int[] lahcList=new int[5000];

        for(int i=0;i<lahcList.length;i++){
            lahcList[i]=penalty;
        }

        List<Student> studentRollBack = new ArrayList<>();
        List<TeachingAssitant> teachingAssitantsRollBack = new ArrayList<>();

        int stuck=0;

        while (System.currentTimeMillis() - startTime < duration) {


            if(phase){
                iteasi++;
                int newPenalty=calculateFitness(jumlahStudent);


                if(penalty>=newPenalty || lahcList[iteasi%lahcList.length]>=newPenalty){

                    if(stuck>=1000){
                       // System.out.println("kena");
                    }

                    if(penalty>newPenalty)
                    stuck=0;
                    //System.out.println(penalty+" new "+newPenalty);
                    lahcList[iteasi%lahcList.length]=newPenalty;
                    penalty=newPenalty;
                    for(int i=0;i<cohort.size();i++){
                        cohort.get(i).setRollback();
                    }


                    studentRollBack=new ArrayList<>();
                    for(int i=0;i<student.size();i++){
                        studentRollBack.add(student.get(i));
                    }

                    teachingAssitantsRollBack=new ArrayList<>();
                    for(int i=0;i<teachingAssitants.size();i++){
                        teachingAssitantsRollBack.add(teachingAssitants.get(i));
                    }

                   // System.out.println(teachingAssitants.size()+" "+student.size());
                }
                else {
                    stuck++;
                    for(int i=0;i<timeSlot.size();i++){
                        timeSlot.get(i).cohort=null;
                    }
                    for(int i=0;i<cohort.size();i++){
                        cohort.get(i).useRollback();
                    }
                    int xx=0;
                    for(int i=0;i<timeSlot.size();i++){
                        if(timeSlot.get(i).cohort!=null) {
                            if (timeSlot.get(i).cohort.timeSlot != timeSlot.get(i)) {
                                System.out.println("beda");
                                System.exit(0);
                            }
                        }else{
                            xx++;
                        }

                    }
                    //System.out.println(xx+" "+timeSlot.size());
                    if(xx+6!=timeSlot.size()){
                        System.out.println(xx+" "+timeSlot.size());
                        System.exit(0);
                    }
                  //  System.out.println(teachingAssitants.size()+" "+student.size());

                    student=new ArrayList<>();
                    teachingAssitants=new ArrayList<>();

                    for(int i=0;i<studentRollBack.size();i++){
                        student.add(studentRollBack.get(i));
                    }
                    for(int i=0;i<teachingAssitantsRollBack.size();i++){
                        teachingAssitants.add(teachingAssitantsRollBack.get(i));
                    }

                   // System.out.println(teachingAssitants.size()+" "+student.size());

                    int x=calculateFitness(jumlahStudent);

                    //System.out.println(penalty+" rollback "+x);
                    if(x!=penalty){
                        System.out.println("kena");
                        System.exit(0);
                    }


                }


                if (bestPenalty > penalty) {
                    if (student.size() == 0 && teachingAssitants.size() == 0) {
                       // System.out.println("best feasible:" + penalty);

                        for(int i=0;i<cohort.size();i++){
                            cohort.get(i).setBest();
                        }


                        bestPenalty = penalty;
                    }

                }

               // System.out.println("penalty:" + penalty + " best:" + bestPenalty + " sisa student:" + student.size() + " sisa teachingAssistance:" + teachingAssitants.size());

            }

            if (iteasi % 100== 0 && phase) {
                System.out.println("iterasi:"+iteasi+" penalty:" + penalty + " best:" + bestPenalty + " sisa student:" + student.size() + " sisa teachingAssistance:" + teachingAssitants.size());
            }

            if (phase) {
                if (Math.random() > 0.5) {
                    moveCohort(false);
                } else {
                    swapCohort(false);
                }


                    moveStudent(false);





                phase = false;

                Collections.shuffle(student);
                Collections.shuffle(teachingAssitants);

                index = 0;//System.out.println("Jumlah Exam Tidak Terjadwal Akhr:"+Main.exams.size());
                indexTeachingAssistanse = 0;
                adaPerubahan = false;
                bestSementara = student.size() + teachingAssitants.size();

            } else {
                boolean perubahan = false;

                if (index < student.size())
                    perubahan = addStudent(index);

                if (indexTeachingAssistanse < teachingAssitants.size()) {
                    if (perubahan) {
                        addTeachingAssitant(indexTeachingAssistanse);
                    } else {
                        perubahan = addTeachingAssitant(indexTeachingAssistanse);
                    }
                }

                if (perubahan) adaPerubahan = true;
                index++;
                indexTeachingAssistanse++;


                if (index >= student.size() && indexTeachingAssistanse >= teachingAssitants.size()) {
                    index = 0;
                    indexTeachingAssistanse = 0;
                    Collections.shuffle(Main.student);
                    Collections.shuffle(teachingAssitants);

                    boolean a = false;


                    int xx=calculateFitness(jumlahStudent);
                    for (int i = 0; i < 100; i++) {
                        if (Math.random() > 0.5) {
                            a = moveCohort(true);//move ini mungkin seharusnya memikirkan penalty
                        } else {
                            a = swapCohort(true);
                        }
                        if (a) {
                            // System.out.println("exit");
                            break;
                        }

                    }
//                    if(a && xx<calculateFitness(jumlahStudent)){
//                        System.out.println("penalty Akhir:"+calculateFitness(jumlahStudent)+" "+xx);
//                    }



                    if(!a)
                    for(int i=0;i<100;i++){
                        a=moveStudent(true);
                        if(a){
                            break;
                        }
                    }

//                    if(a && xx!=calculateFitness(jumlahStudent)){
//                        System.out.println("penalty Akhir:"+calculateFitness(jumlahStudent)+" "+xx);
//                    }




                    //move student and swap student


                    if (!a && !adaPerubahan) {
                        indexStuck = 1000;
                        //System.out.println("kenaa");
                    }

                    if (Main.student.size() + teachingAssitants.size() >= bestSementara) indexStuck++;
                    else {
                        indexStuck = 0;
                        bestSementara = Main.student.size() + teachingAssitants.size();

                    }


                    if (indexStuck > 10) {
                        phase = true;
                        indexStuck = 0;
                    } else {

                        adaPerubahan = false;


                    }

                }
            }


        }
    }

    public static boolean moveStudent(boolean t){
        int x=calculateFitness(jumlahStudent);
        Cohort c = cohort.get(r.nextInt(cohort.size()));

        if(c.students.size()==0)return false;

        Student s=c.students.get(r.nextInt(c.students.size()));


        List<Cohort> listDestination=new ArrayList<>();

        for(int i=0;i<cohort.size();i++){
            if(cohort.get(i)!=c && cohort.get(i).timeSlot!=null){
                if(s.cekAvail(cohort.get(i).timeSlot.timeSlotId)){
                    listDestination.add(cohort.get(i));
                }
            }
        }

        if(listDestination.size()==0)return false;

        Cohort destination=cohort.get(r.nextInt(listDestination.size()));

        c.students.remove(s);
        destination.students.add(s);

        int xy=calculateFitness(jumlahStudent);

       if(xy<x || t){
           return true;
       }else{
           c.students.add(s);
           destination.students.remove(s);
           if(calculateFitness(jumlahStudent)!=x){
               System.exit(0);
           }
           return false;
       }





    }

    public static void findFeasible(){


        boolean phase = true;
        int index = 0;
        int indexTeachingAssistanse = 0;
        int indexStuck = 0;
        int best = student.size() + teachingAssitants.size();
        boolean adaPerubahan = false;
        int bestSementara = 0;
        double penurun = 0.3;
        probabilitasEksplorasi = 1 - penurun;
        int indexStuckGlobal = 0;
        int bestSolution2 = student.size() + teachingAssitants.size();


        iterasi = 0;


        int awalStudent = student.size();

        int awalTeachingAssistant = teachingAssitants.size();

         while (student.size() > 0 || teachingAssitants.size() > 0) {


        if (iterasi % 1000 == 0) {
            // System.out.println("iterasi:" + iterasi + " best:" + best + " saat ini:" + student.size() + " " + probabilitasEksplorasi);

            int cekJumlahStudent = student.size();

//             for(int i=0;i<cohort.size();i++){
//                 cekJumlahStudent+=cohort.get(i).students.size();
//             }
//
//             if(cekJumlahStudent!=awalStudent){
//                 System.out.println("beda");
//                 System.exit(0);
//             }
//
////             for(int i=0;i<cohort.size();i++){
////                 System.out.println(cohort.get(i).id+" "+cohort.get(i).students.size()+" "+cohort.get(i).timeSlot.room.kapasitas+" "+cohort.get(i).timeSlot.timeSlotId);
////             }
////
////             for(int i=0;i<student.size();i++){
////                 System.out.println(Arrays.toString(student.get(i).avail));
////             }


            int cekJumlahTeachingAssistant = teachingAssitants.size();
            // System.out.println(cekJumlahTeachingAssistant);
            for (int i = 0; i < cohort.size(); i++) {
                cekJumlahTeachingAssistant += cohort.get(i).teachingAssitants.size();
            }

            if (cekJumlahTeachingAssistant != awalTeachingAssistant) {
                System.exit(0);
            }

        }

        iterasi++;

        if (student.size() + teachingAssitants.size() < best) {
            best = student.size() + teachingAssitants.size();
        }


        if (student.size() + teachingAssitants.size() < bestSolution2) {
            bestSolution2 = student.size() + teachingAssitants.size();
            indexStuckGlobal = 0;
        } else {
            indexStuckGlobal++;
        }

        if (indexStuckGlobal > 100000) {
            penurun *= 0.85;
            probabilitasEksplorasi = 1 - penurun;

            if (probabilitasEksplorasi > 0.991) {
                // exploitasi=false;
                penurun = 0.3;
                probabilitasEksplorasi = 1 - penurun;
            }
            indexStuckGlobal = 0;
            bestSolution2 = student.size() + teachingAssitants.size();
        }

        if (phase) {

            if (Math.random() > 0.5) {
                moveCohort(false);
            } else {

                swapCohort(false);
            }


            phase = false;

            Collections.shuffle(student);
            Collections.shuffle(teachingAssitants);

            index = 0;//System.out.println("Jumlah Exam Tidak Terjadwal Akhr:"+Main.exams.size());
            indexTeachingAssistanse = 0;
            adaPerubahan = false;
            bestSementara = student.size() + teachingAssitants.size();

        } else {
            boolean perubahan = false;

            if (index < student.size())
                perubahan = addStudent(index);

            if (indexTeachingAssistanse < teachingAssitants.size()) {
                if (perubahan) {
                    addTeachingAssitant(indexTeachingAssistanse);
                } else {
                    perubahan = addTeachingAssitant(indexTeachingAssistanse);
                }
            }

            if (perubahan) adaPerubahan = true;
            index++;
            indexTeachingAssistanse++;


            if (index >= student.size() && indexTeachingAssistanse >= teachingAssitants.size()) {
                index = 0;
                indexTeachingAssistanse = 0;
                Collections.shuffle(Main.student);
                Collections.shuffle(teachingAssitants);

                boolean a = false;
                for (int i = 0; i < 100; i++) {
                    if (Math.random() > 0.5) {
                        a = moveCohort(true);
                    } else {
                        a = swapCohort(true);
                    }
                    if (a) {
                        // System.out.println("exit");
                        break;
                    }

                }

                if (!a && !adaPerubahan) {
                    indexStuck = 1000;
                    //System.out.println("kenaa");
                }

                if (Main.student.size() + teachingAssitants.size() >= bestSementara) indexStuck++;
                else {
                    indexStuck = 0;
                    bestSementara = Main.student.size() + teachingAssitants.size();

                }


                if (indexStuck > 20) {
                    phase = true;
                    indexStuck = 0;
                } else {

                    adaPerubahan = false;


                }

            }
        }


    }



        // System.out.println("keluar");

}

    public static int calculateFitness(int jumlahStudent){

        int penalty=0;
        int average=jumlahStudent/6;

        for(int i=0;i<cohort.size();i++){
            if(cohort.get(i).timeSlot.timeSlotId%4==0){
                penalty+=cohort.get(i).students.size();
                penalty+=cohort.get(i).teachingAssitants.size();
            }
            if(cohort.get(i).timeSlot.timeSlotId%4==3){
                penalty+=cohort.get(i).students.size()*2;
                penalty+=cohort.get(i).teachingAssitants.size()*2;
            }

            if(cohort.get(i).students.size()!=average){
                int distribute=cohort.get(i).students.size()-average>0?cohort.get(i).students.size()-average:average-cohort.get(i).students.size();
                penalty+=distribute;
            }
        }

        penalty+=student.size()*50;
        penalty+=teachingAssitants.size()*50;

        //System.out.println(student.size()+" "+teachingAssitants.size());

        return penalty;

    }

    public static void prarposesData() {
        for (int i = 0; i < student.size(); i++) {
            student.get(i).praproses();
        }

    }

    public static boolean addTeachingAssitant(int i) {


        Collections.shuffle(cohort);

        boolean t = true;
        for (int j = 0; j < cohort.size(); j++) {
            if (cohort.get(j).teachingAssitants.size() < 2) {
                t = false;
                break;
            }
        }

        for (int j = 0; j < cohort.size(); j++) {
            if (teachingAssitants.get(i).cekAvail(cohort.get(j).timeSlot.timeSlotId)) {
                if (cohort.get(j).teachingAssitants.size() < 2 || t) {

                    cohort.get(j).teachingAssitants.add(teachingAssitants.get(i));
                    teachingAssitants.remove(i);
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean addStudent(int i) {


        Collections.shuffle(cohort);

        for (int j = 0; j < cohort.size(); j++) {
            if (student.get(i).cekAvail(cohort.get(j).timeSlot.timeSlotId)) {
                if (cohort.get(j).timeSlot.room.kapasitas > cohort.get(j).students.size()) {
                    cohort.get(j).students.add(student.get(i));
                    student.remove(i);
                    return true;
                }
            }
        }

//            if(!berhasil && match.size()>0){
//                int ra=match.get(r.nextInt(match.size()));
//                Student x=cohort.get(ra).students.get(r.nextInt(cohort.get(ra).students.size()));
//                cohort.get(ra).students.remove(x);
//                cohort.get(ra).students.add(student.get(i));
//                student.remove(i);
//                student.add(x);
//            }


        return false;

    }

    public static boolean swapCohort(boolean t) {
        boolean tt=student.size()==0 && teachingAssitants.size()==0?true:false;

        Cohort c = cohort.get(r.nextInt(cohort.size()));
        List<TimeSlot> ts = new ArrayList<>();
        List<Integer> numberConflict = new ArrayList<>();

        for (int i = 0; i < cohort.size(); i++) {
            if (cohort.get(i) != c) {
                ts.add(cohort.get(i).timeSlot);

                int conflict = 0;

                conflict += c.tesKonfilk(cohort.get(i).timeSlot.timeSlotId);
                conflict += cohort.get(i).tesKonfilk(c.timeSlot.timeSlotId);

                numberConflict.add(conflict);

            }
        }


        List<SimpleEntry<TimeSlot, Integer>> combined = new ArrayList<>();
        for (int i = 0; i < ts.size(); i++) {
            combined.add(new SimpleEntry<>(ts.get(i), numberConflict.get(i)));
        }


        Collections.sort(combined, Comparator.comparing(SimpleEntry::getValue));

        ts.clear(); // Clear the original lists
        numberConflict.clear();

        if (t) {
            if (combined.get(0).getValue() == 0) {
                int xx = 0;

                for (int i = 1; i < combined.size(); i++) {
                    if (combined.get(i).getValue() == 0) {
                        xx = i;
                    } else {
                        break;
                    }
                }

                //if(xx==0)System.out.println("cuma 0");


                int x=0;
                if(tt)x=calculateFitness(jumlahStudent);

                TimeSlot terpilih = combined.get(r.nextInt(xx + 1)).getKey();
                TimeSlot terpilih2 = c.timeSlot;

                Cohort swap1 = terpilih.cohort;


                terpilih.cohort = c;
                terpilih2.cohort = swap1;

                c.timeSlot = terpilih;
                swap1.timeSlot = terpilih2;


                if(tt)
                if(calculateFitness(jumlahStudent)<=x){
                    c.cekKesesuaian();
                    swap1.cekKesesuaian();


                    // System.out.println("berhasil");

                    return true;
                }else {
                    terpilih.cohort=swap1;
                    terpilih2.cohort=c;

                    c.timeSlot=terpilih2;
                    swap1.timeSlot=terpilih;



                    return false;
                }

                c.cekKesesuaian();
                swap1.cekKesesuaian();


                // System.out.println("berhasil");

                return true;


            } else {
                return false;
            }

        } else {
            TimeSlot terpilih = combined.get((int) ((1 - probabilitasEksplorasi) * combined.size())).getKey();


            TimeSlot terpilih2 = c.timeSlot;

            Cohort swap1 = terpilih.cohort;


            terpilih.cohort = c;
            terpilih2.cohort = swap1;

            c.timeSlot = terpilih;
            swap1.timeSlot = terpilih2;

            c.removeKonflik();
            swap1.removeKonflik();

            c.cekKesesuaian();
            swap1.cekKesesuaian();


            return true;
        }


    }

    public static boolean moveCohort(boolean t) {

        boolean tt=student.size()==0 && teachingAssitants.size()==0?true:false;


        Cohort c = cohort.get(r.nextInt(cohort.size()));

        List<TimeSlot> ts = new ArrayList<>();
        List<Integer> numberConflict = new ArrayList<>();

        for (int i = 0; i < timeSlot.size(); i++) {
            if (timeSlot.get(i).cohort == null) {

                if (timeSlot.get(i) == c.timeSlot) {
                    System.out.println("sama");
                    System.exit(0);
                }

                ts.add(timeSlot.get(i));
                numberConflict.add(c.tesKonfilk(timeSlot.get(i).timeSlotId));
            }
        }

        List<SimpleEntry<TimeSlot, Integer>> combined = new ArrayList<>();
        for (int i = 0; i < ts.size(); i++) {
            combined.add(new SimpleEntry<>(ts.get(i), numberConflict.get(i)));
        }


        Collections.sort(combined, Comparator.comparing(SimpleEntry::getValue));

        ts.clear(); // Clear the original lists
        numberConflict.clear();


        if (t) {
            if (combined.get(0).getValue() == 0) {
                int xx = 0;

                for (int i = 1; i < combined.size(); i++) {
                    if (combined.get(i).getValue() == 0) {
                        xx = i;
                    } else {
                        break;
                    }
                }


                TimeSlot awal = c.timeSlot;
                TimeSlot terpilih = combined.get(r.nextInt(xx + 1)).getKey();

                // System.out.println("slot Awal:"+c.timeSlot.room.id+" "+c.timeSlot.timeSlotId);


                int prevPen=0;

                if(tt)prevPen=calculateFitness(jumlahStudent);

                c.timeSlot.cohort = null;
                c.timeSlot = terpilih;
                terpilih.cohort = c;

                if(tt)
                if(prevPen<calculateFitness(jumlahStudent)){
                    c.timeSlot.cohort=null;
                    c.timeSlot=awal;
                    awal.cohort=c;

                    return false;
                }

                // System.out.println("slot Akhir:"+c.timeSlot.room.id+" "+c.timeSlot.timeSlotId);

                // System.out.println("berhasil");

                if (awal == terpilih) {
                    System.out.println("sama ini");
                    System.exit(0);
                }
                c.cekKesesuaian();

                return true;
            } else {
                return false;
            }


        } else {

            TimeSlot terpilih = combined.get((int) ((1 - probabilitasEksplorasi) * combined.size())).getKey();


            c.timeSlot.cohort = null;
            c.timeSlot = terpilih;
            terpilih.cohort = c;

            c.removeKonflik();
            c.cekKesesuaian();

        }

        return false;
    }

    public static void printHasil(int data, int pen, double dur, int ver) throws IOException {


        myWriter = new FileWriter(path + "/Hasil/" + data+"-"+ver+".csv");
        Main.myWriter.write("Penalty:"+pen+", duration:"+dur+"\n\n");
        for (int i = 0; i < cohort.size(); i++) {
            cohort.get(i).printHasil();
        }
        myWriter.close();

    }

}