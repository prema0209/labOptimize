import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    public static int jumlahTeachingAssistance;

    public static double firstFeasibleTime;
    public static double bestFeasibleTime;

    public static List<Student> studentRollBackBest;
    public static List<TeachingAssitant> teachingAssitantsRollBackBest;


    public static List<Student> studentCheckPoint;
    public static List<TeachingAssitant> teachingAssitantsCheckPoint;



    public static void main(String[] args) throws InterruptedException, IOException {

        Scanner sc = new Scanner(System.in);

        r = new Random();





        GetDataset input = new GetDataset();

        path = "/Users/igustiagungpremananda/IdeaProjects/LabTimetabling/";
        path= "C:/Users/igust/IdeaProjects/LabOptimize/";

        System.out.println("dataset ke?");



        int data = sc.nextInt();


        System.out.println("percobaan ke?");



        int ver = sc.nextInt();

        int penalty=0;




        int ver2=ver;

        for(int ik=0;ik<1;ik++) {

            ver=ver2+(ik*10);
            System.out.println("ver:"+ver);
            room = new ArrayList<>();
            student = new ArrayList<>();
            teachingAssitants = new ArrayList<>();
            firstFeasibleTime=0;
            bestFeasibleTime=0;
          //  cohort=new ArrayList<>();

            input.readRoom(path + "Dataset/" + data + "/Room Timeslot.csv");
            input.readStudent(path + "Dataset/" + data + "/Student Timeslot.csv");
            input.readTeachingAssistant(path + "Dataset/" + data + "/Teaching Assistant Timeslot.csv");
            //input.readName(path + "Dataset/" + data + "/cariKelas.csv");


            // System.exit(0);


            for (int i = 0; i < student.size(); i++) {
                if (student.get(i).cekALlFalse()) {
                    System.out.println( student.get(i).nama);
                    student.remove(student.get(i));

                    i--;
                }
            }

            System.out.println("jumlah student:"+student.size());

            jumlahStudent = student.size();
            jumlahTeachingAssistance = teachingAssitants.size();
//            System.out.println(teachingAssitants.size());
//
   //        System.exit(0);

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





            cohort = new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                cohort.add(new Cohort(i));
            }


            for (int i = 0; i < cohort.size(); i++) {
                //cohort.get(i).printJadwal();
            }


            // System.out.println("jumlah murid:" + jumlahStudent);


            prarposesData();




            System.out.println("1. Fest 2. Opt:");
            int ss=sc.nextInt();

            long startTime = System.currentTimeMillis();
            if(ss==1){
                Collections.shuffle(timeSlot);
                for (int i = 0; i < 6; i++) {

                    while (true) {
                        int x = r.nextInt(timeSlot.size());

                        if (timeSlot.get(x).cohort == null) {
                            timeSlot.get(x).cohort = cohort.get(i);
                            cohort.get(i).timeSlot = timeSlot.get(x);
                            break;
                        }
                    }
                }

                GenerateFeasibleSolution fes=new GenerateFeasibleSolution();
                fes.createFeasibleSolution();


            }else{

                ReadSol readSol=new ReadSol();

                readSol.readSOl(path+"Hasil/Solusi Awal/"+data);

//                teachingAssitants=new ArrayList<>();
//                student=new ArrayList<>();


                ILSOptimize op=new ILSOptimize();

                op.optimize();

            }




            //feasibleOptimize(startTime);

          // feasibleOptimizeSA(startTime);

         //   feasibleOptimizeLAHC(startTime);


           //feasibleOptimizeILSStandart(startTime);

//            if(bestFeasibleTime>0){
//                int cekStudent = 0;
//                for (int i = 0; i < cohort.size(); i++) {
//                    cohort.get(i).useBest();
//                    cekStudent += cohort.get(i).students.size();
//                }
//
//                if (jumlahStudent != cekStudent) System.exit(0);
//                student = new ArrayList<>();
//                teachingAssitants = new ArrayList<>();
//            }else {
//                System.out.println("no feasible solution");
//            }


            System.out.println("feasible:"+cekFeasible());
            penalty = calculateFitness();

            System.out.println(student.size()+" "+teachingAssitants.size());


            System.out.println("penalty akhir:" + penalty);


            long endTime = System.currentTimeMillis();


            double runtime = (double) (endTime - startTime) / (1000);

            System.out.println("Elapsed Time in seconds: " + runtime);


            Collections.sort(cohort);

            printHasil(data, penalty, runtime, ver);

            printHasil2(data, penalty, runtime, ver);

        }
        //System.out.println(student.size());

    }



    public static boolean cekFeasible(){
        List<Student> stu=new ArrayList<>();
        List<TeachingAssitant> tas=new ArrayList<>();
        List<TimeSlot> ts=new ArrayList<>();

        for(int i=0;i<cohort.size();i++){
            if(ts.contains(cohort.get(i).timeSlot)){
                System.out.println("kena 1");
                return false;
            }else{
                ts.add(cohort.get(i).timeSlot);
            }

            if(cohort.get(i).students.size()>cohort.get(i).timeSlot.room.kapasitas){//check kapasity const
                System.out.println("kena 2");
                return false;
            }

            for(int j=0;j<cohort.get(i).students.size();j++){//check one student one cohort
                if(stu.contains(cohort.get(i).students.get(j))){
                    System.out.println("kena 3");
                    return false;
                }else{
                    stu.add(cohort.get(i).students.get(j));
                    if(!cohort.get(i).students.get(j).cekAvail(cohort.get(i).timeSlot.timeSlotId)){//check avail timeslot
                        System.out.println("kena 4");
                        return false;
                    }
                }

            }
            //System.out.println(cohort.get(i).teachingAssitants.size());
            if(cohort.get(i).teachingAssitants.size()<2){

                // System.out.println("kena 5");
                return false;
            }

            for(int j=0;j<cohort.get(i).teachingAssitants.size();j++){
                if(tas.contains(cohort.get(i).teachingAssitants.get(j))){
                    System.out.println("kena 6");
                    return false;
                }
                else{
                    tas.add(cohort.get(i).teachingAssitants.get(j));
                    if(!cohort.get(i).teachingAssitants.get(j).cekAvail(cohort.get(i).timeSlot.timeSlotId)){
                        System.out.println("kena 7");
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public static void feasibleOptimizeLAHC(long startTime) throws IOException {
        long duration = 600000;

        duration=60000;
//        duration=300000;
//        duration=600000;


        int iterasi=0;



        int penalty=calculateFitness();
        int best=penalty;
        int[] lahcList;
          lahcList=new int[2000000];
        //  lahcList=new int[10000000];
      //  lahcList=new int[20000000];


        for(int i=0;i<lahcList.length;i++){
            lahcList[i]=penalty;
        }
        List<String> movement=new ArrayList<>();
        List<String> bestMovement=new ArrayList<>();

        int indexSkip=0;

        while (System.currentTimeMillis() - startTime < duration) {
            iterasi++;


            if(iterasi%10000000==0){
                System.out.println("penalty:"+penalty+" best:"+best+" durasi:"+(duration-(System.currentTimeMillis() - startTime)));
                System.out.println(student.size()+" "+teachingAssitants.size());
            }

            if (Math.random() > 0.5 && (student.size() !=0 || teachingAssitants.size() !=0)) {
                //System.out.println("kenma");

                if (student.size() > 0 && teachingAssitants.size() > 0) {
                    if (Math.random() > 0.5) {
                        addStudent(r.nextInt(student.size()));
                        //System.out.println("kenma: "+teachingAssitants.size());
                    } else {
                        addTeachingAssitant(r.nextInt(teachingAssitants.size()));
                    }
                } else if (student.size() > 0) {
                    addStudent(r.nextInt(student.size()));
                    //System.out.println("kenma: "+teachingAssitants.size());
                } else {
                    addTeachingAssitant(r.nextInt(teachingAssitants.size()));

                }

            } else {
                double x=Math.random();
                // System.out.println("kenma");




                if(x<0.25){
                    moveCohort(Math.random()>0.1?true:false, null);

                }else if(x<0.5){
                    swapCohort(Math.random()>0.1?true:false, null);

                }
                else if(x<0.75){
                    moveStudent(Math.random()>0.1?true:false, null);

                }
                else{
                    swapStudent(Math.random()>0.1?true:false, null);

                }


            }

            int newPenalty=calculateFitness();


            if(penalty>=newPenalty || lahcList[iterasi%lahcList.length]>=newPenalty){

                if((indexSkip%10000==0)){
                    movement.add(newPenalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                }else{

                }
                indexSkip++;

                penalty=newPenalty;
                lahcList[iterasi%lahcList.length]=newPenalty;
                setRollback();
                if(teachingAssitants.size()==0 && student.size()==0){
                    //System.out.println("feasible");
                    if(best>penalty){
                        best=penalty;
                        bestFeasibleTime=(double)(System.currentTimeMillis() - startTime)/(1000);
                        movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                        for(int i=0;i<cohort.size();i++){
                            cohort.get(i).setBest();
                        }
                    }
                }


            }else{
                useRollback();
            }
        }


        printMovement(movement);
        printBestMovement(bestMovement);
        System.out.println("best");

    }
    public static void feasibleOptimizeSA(long startTime) throws InterruptedException, IOException {
        long duration = 600000;

          duration=60000;
       //  duration=300000;
//        duration=600000;

        List<String> movement=new ArrayList<>();
        List<String> bestMovement=new ArrayList<>();
        int iterasi=0;



        //setting one minuet
        double temp=10000;
        double coolingRate=0.00000035;

        //coolingRate=0.00000008;

       // coolingRate=0.000000045;



        int penalty=calculateFitness();
        int best=penalty;

        int indexSkip=0;
        while (temp>1) {
            temp *= 1 - coolingRate;
            iterasi++;


            if(iterasi%10000000==0){
                System.out.println("penalty:"+penalty+" best:"+best+" durasi:"+(duration-(System.currentTimeMillis() - startTime)));
                System.out.println(student.size()+" "+teachingAssitants.size());
                System.out.println("temp:"+temp);
            }

            if (Math.random() > 0.5 && (student.size() !=0 || teachingAssitants.size() !=0)) {
                //System.out.println("kenma");

                if (student.size() > 0 && teachingAssitants.size() > 0) {
                    if (Math.random() > 0.5) {
                        addStudent(r.nextInt(student.size()));
                        //System.out.println("kenma: "+teachingAssitants.size());
                    } else {
                        addTeachingAssitant(r.nextInt(teachingAssitants.size()));
                    }
                } else if (student.size() > 0) {
                    addStudent(r.nextInt(student.size()));
                    //System.out.println("kenma: "+teachingAssitants.size());
                } else {
                    addTeachingAssitant(r.nextInt(teachingAssitants.size()));

                }

            } else {
                double x=Math.random();
               // System.out.println("kenma");




                if(x<0.25){
                    moveCohort(Math.random()>0.1?true:false, null);

                }else if(x<0.5){
                    swapCohort(Math.random()>0.1?true:false, null);

                }
                else if(x<0.75){
                   moveStudent(Math.random()>0.1?true:false, null);

                }
                else{
                    swapStudent(Math.random()>0.1?true:false, null);

                }


            }

            int newPenalty=calculateFitness();

            double remaining=(System.currentTimeMillis() - startTime);

            double y=(double) ((penalty-newPenalty))/(temp);

            double x=Math.exp(y);




         //   System.out.println(duration+" "+(duration-remaining));

             if(penalty<newPenalty) {
                 //System.out.println(y+" "+penalty+" "+newPenalty+" "+x);
             }



          // Thread.sleep(50);
            if(penalty>=newPenalty || Math.random()<x){

                if((indexSkip%10000==0)){
                    movement.add(newPenalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                }else{

                }
                indexSkip++;


                penalty=newPenalty;
              //  lahcList[iterasi%lahcList.length]=newPenalty;
                setRollback();
                if(teachingAssitants.size()==0 && student.size()==0){
                    //System.out.println("feasible");
                    if(best>penalty){
                        best=penalty;
                        bestFeasibleTime=(double)(System.currentTimeMillis() - startTime)/(1000);
                        movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                        for(int i=0;i<cohort.size();i++){
                            cohort.get(i).setBest();
                        }
                    }
                }


            }else{
                useRollback();
            }
        }

        printMovement(movement);
        printBestMovement(bestMovement);

        System.out.println("best");

    }




    public static void feasibleOptimizeILSStandart(long startTime) throws IOException {
        long duration = 600000;

        duration=60000;
    //   duration=300000;
  //      duration=600000;


        int iterasi=0;



        int penalty=calculateFitness();
        int best=penalty;



        List<String> movement=new ArrayList<>();
        List<String> bestMovement=new ArrayList<>();

        int indexSkip=0;

        boolean phase=true; //true=local search
        int stuck=0;

        int newPenalty=penalty;
        while (System.currentTimeMillis() - startTime < duration) {
            iterasi++;


            if(iterasi%10000000==0){
                System.out.println("penalty:"+penalty+" best:"+best+" durasi:"+(duration-(System.currentTimeMillis() - startTime)));
                System.out.println(student.size()+" "+teachingAssitants.size());
            }

            if (Math.random() > 0.5 && (student.size() !=0 || teachingAssitants.size() !=0)) {
                //System.out.println("kenma");

                if (student.size() > 0 && teachingAssitants.size() > 0) {
                    if (Math.random() > 0.5) {
                        addStudent(r.nextInt(student.size()));
                        //System.out.println("kenma: "+teachingAssitants.size());
                    } else {
                        addTeachingAssitant(r.nextInt(teachingAssitants.size()));
                    }
                } else if (student.size() > 0) {
                    addStudent(r.nextInt(student.size()));
                    //System.out.println("kenma: "+teachingAssitants.size());
                } else {
                    addTeachingAssitant(r.nextInt(teachingAssitants.size()));

                }

            } else {
                double x=Math.random();
                // System.out.println("kenma");
                if(x<0.25){
                    moveCohort(phase, null);

                }else if(x<0.5){
                    swapCohort(phase, null);

                }
                else if(x<0.75){
                    moveStudent(phase, null);

                }
                else{
                    swapStudent(phase, null);
                }
            }




            boolean terima=false;


            if(phase){


                if(newPenalty>=calculateFitness()){
                    stuck++;
                }
                else {
                    stuck=0;
                }



                if(stuck>1000){
                    phase=false;
                    stuck=0;
                }

            }else{
                phase=true;

//                if(teachingAssitants.size()==0 && student.size()==0){
//                    //System.out.println("feasible");
//                    if(best>penalty){
//                        best=penalty;
//                        bestFeasibleTime=(double)(System.currentTimeMillis() - startTime)/(1000);
//                    //    movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
//                        for(int i=0;i<cohort.size();i++){
//                            cohort.get(i).setBest();
//                        }
//                    }
//                }
            }

            newPenalty=calculateFitness();


            if(!phase){

                if(penalty>=newPenalty){// || Math.random()>pengurang){//|| lahcList[iteasi%lahcList.length]>=newPenalty){


                    //System.out.println("keterima"+penalty+" "+newPenalty);
                    penalty=newPenalty;



                    setRollback();
                    // System.out.println(teachingAssitants.size()+" "+student.size());

                    if(teachingAssitants.size()==0 && student.size()==0){

                        if(best>penalty) {
                            bestFeasibleTime = (double) (System.currentTimeMillis() - startTime) / (1000);
                            //    movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                            for(int i=0;i<cohort.size();i++){
                                cohort.get(i).setBest();
                            }
                            best=penalty;


                        }
                    }
                }
                else {//if (Math.exp(((penalty -newPenalty)*duration )/ ((System.currentTimeMillis() - startTime))) >= Math.random())  {
                    useRollback();

                    //System.out.println("ditolak"+penalty+" "+newPenalty);


                    // System.out.println(teachingAssitants.size()+" "+student.size());
                }

                if(calculateFitness()!=penalty){
                    System.exit(0);
                }

                movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));

            }

//            if(terima){
//                if((indexSkip%5000==0)){
//                    movement.add(newPenalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
//                }else{
//
//                }
//                indexSkip++;
//            }
        }


        printMovement(movement);
        printBestMovement(bestMovement);
        System.out.println("best");

    }
    public static void useRollback(){
        for(int i=0;i<timeSlot.size();i++){
            timeSlot.get(i).cohort=null;
        }
        for(int i=0;i<cohort.size();i++){
            cohort.get(i).useRollback();
        }

        student=new ArrayList<>();
        teachingAssitants=new ArrayList<>();

        for(int i=0;i<studentRollBack.size();i++){
            student.add(studentRollBack.get(i));
        }
        for(int i=0;i<teachingAssitantsRollBack.size();i++){
            teachingAssitants.add(teachingAssitantsRollBack.get(i));
        }
    }

    public static void setRollback(){
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
    }

    public static void useCheckPoint(){
        for(int i=0;i<timeSlot.size();i++){
            timeSlot.get(i).cohort=null;
        }
        for(int i=0;i<cohort.size();i++){
            cohort.get(i).useCheckPoint();
        }

        student=new ArrayList<>();
        teachingAssitants=new ArrayList<>();

        for(int i=0;i<studentCheckPoint.size();i++){
            student.add(studentCheckPoint.get(i));
        }
        for(int i=0;i<teachingAssitantsCheckPoint.size();i++){
            teachingAssitants.add(teachingAssitantsCheckPoint.get(i));
        }
    }

    public static void setCheckPoint(){
        for(int i=0;i<cohort.size();i++){
            cohort.get(i).setCheckPoint();
        }


        studentCheckPoint=new ArrayList<>();
        for(int i=0;i<student.size();i++){
            studentCheckPoint.add(student.get(i));
        }

        teachingAssitantsCheckPoint=new ArrayList<>();
        for(int i=0;i<teachingAssitants.size();i++){
            teachingAssitantsCheckPoint.add(teachingAssitants.get(i));
        }
    }

    public static void setBest(){

        for(int i=0;i<cohort.size();i++){
            cohort.get(i).setBest();
        }
    }


    public static void useBest(){
        for(int i=0;i<timeSlot.size();i++){
            timeSlot.get(i).cohort=null;
        }
        for(int i=0;i<cohort.size();i++){
            cohort.get(i).useBest();
        }

        student=new ArrayList<>();
        teachingAssitants=new ArrayList<>();


    }

    public static List<Student> studentRollBack;
    public static List<TeachingAssitant> teachingAssitantsRollBack;


    public static double threshold;

    public static void feasibleOptimize(long startTime) throws IOException {

        List<String> movement=new ArrayList<>();
        List<String> bestMovement=new ArrayList<>();

        long duration ;


        //duration=10000;
        duration=60000;
       // duration=300000;
        //duration=600000;
        //duration=600000;

         //threshold=1.1;

        boolean phase=true;

        int index = 0;
        int indexTeachingAssistanse = 0;
        int indexStuck = 0;

        boolean adaPerubahan = false;
        int bestSementara = 0;


        int jumlahStudent=student.size();


        int penalty=calculateFitness();


        int bestPenalty=penalty;

        int iteasi=1;

        int[] lahcList=new int[50000];

        for(int i=0;i<lahcList.length;i++){
            lahcList[i]=penalty;
        }




        int stuck=0;

        int batasStuck;
        double pengurang=0.5;

        boolean bestPenaltyFeasible=false;


        threshold=0;

        double preRun=0.1*duration;

        int it=0;
        int penAwal=penalty;

        int indexSkip=0;

        while(System.currentTimeMillis() - startTime < preRun){

            it++;


            if (Math.random() > 0.5 && (student.size() !=0 || teachingAssitants.size() !=0)) {
                //System.out.println("kenma");

                if (student.size() > 0 && teachingAssitants.size() > 0) {
                    if (Math.random() > 0.5) {
                        addStudent(r.nextInt(student.size()));
                    } else {
                        addTeachingAssitant(r.nextInt(teachingAssitants.size()));
                    }
                } else if (student.size() > 0) {
                    addStudent(r.nextInt(student.size()));
                } else {
                    addTeachingAssitant(r.nextInt(teachingAssitants.size()));
                }

            } else {
               // System.out.println("kenma");
                double x=Math.random();

                if(x<0.25){
                    moveCohort(false, null);
                }else if(x<0.5){
                    swapCohort(false, null);
                }
                else if(x<0.75){
                    moveStudent(false, null);
                }
                else{
                    swapStudent(false, null);
                }


            }
            int newpen=calculateFitness();
            int selisih=penAwal-newpen>=0?penAwal-newpen:newpen-penAwal;

            if((indexSkip%10000==0)){
                movement.add(newpen+";"+((double)(System.currentTimeMillis() - startTime)/1000));
            }else{

            }
            indexSkip++;

            threshold+=selisih;
            penAwal=newpen;
    }


        threshold=threshold/it;

      //  duration=duration-(long) preRun;
        //duration=10000;
        System.out.println(threshold+" "+duration);

       // System.exit(0);


        //threshold=1.1;


        while (System.currentTimeMillis() - startTime < duration) {


            if(teachingAssitants.size()==0 && student.size()==0 && firstFeasibleTime==0){
               // System.out.println("first time feasible");
                firstFeasibleTime=(double)(System.currentTimeMillis() - startTime)/(1000);

            }

            if(phase){
                iteasi++;
                int newPenalty=calculateFitness();
                //stuck++;

                if((indexSkip%1000==0)){
                    movement.add(newPenalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                }else{

                }
                indexSkip++;


                if(penalty>=newPenalty || (penalty<bestPenalty && bestPenaltyFeasible?newPenalty<=bestPenalty+threshold:newPenalty<=penalty+threshold)){// || Math.random()>pengurang){//|| lahcList[iteasi%lahcList.length]>=newPenalty){

                    if(stuck>=10000){
                       // System.out.println("kena");
                        pengurang+=0.1;
                        if(pengurang>1)pengurang=0.5;
                        stuck=0;
                    }

                    if(penalty>newPenalty)
                    //stuck=0;
                    //System.out.println(penalty+" new "+newPenalty);
                    lahcList[iteasi%lahcList.length]=newPenalty;

//                    if(penalty>newPenalty){
//                        movement.add(newPenalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
//                    }

                    penalty=newPenalty;


                    setRollback();
                   // System.out.println(teachingAssitants.size()+" "+student.size());
                }
                else {//if (Math.exp(((penalty -newPenalty)*duration )/ ((System.currentTimeMillis() - startTime))) >= Math.random())  {
                    useRollback();
                   // System.out.println(teachingAssitants.size()+" "+student.size());
                }


                if (bestPenalty > penalty) {
                    if (student.size() == 0 && teachingAssitants.size() == 0) {
                    //s    System.out.println("best feasible:" + penalty);

                        for(int i=0;i<cohort.size();i++){
                            cohort.get(i).setBest();
                        }

                        bestFeasibleTime=(double)(System.currentTimeMillis() - startTime)/(1000);
                        //System.out.println("best feasible");
                        movement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                        bestPenaltyFeasible=true;
                        bestPenalty = penalty;
                        stuck=0;
                        bestMovement.add(penalty+";"+((double)(System.currentTimeMillis() - startTime)/1000));
                    }else{
                        stuck++;
                    }


                }else{
                  //  System.out.println("koi");
                    stuck++;
                }

               // System.out.println("penalty:" + penalty + " best:" + bestPenalty + " sisa student:" + student.size() + " sisa teachingAssistance:" + teachingAssitants.size());

            }

            if (iteasi % 1000000== 0 && phase) {
                System.out.println("iterasi:"+iteasi+" penalty:" + penalty + " best:" + bestPenalty + " sisa student:" + student.size() + " sisa teachingAssistance:" + teachingAssitants.size()+" "+pengurang+"  "+stuck);
                System.out.println("sisa:");
                for(int i=0;i<student.size();i++){
                    System.out.println(student.get(i).nama);
                }
            }

            if(iteasi%100000==0){

            }

            if (phase) {


                double x=r.nextInt(2);
                if(x==0){
                    moveCohort(false, null);
                }else if(x==1){
                    swapCohort(false, null);
                }




                phase = false;

                Collections.shuffle(student);
                Collections.shuffle(teachingAssitants);

                index = 0;//System.out.println("Jumlah Exam Tidak Terjadwal Akhr:"+Main.exams.size());
                indexTeachingAssistanse = 0;
                adaPerubahan = false;
                bestSementara = calculateFitness();

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


                    boolean a=false;
                    boolean tt=student.size()==0 && teachingAssitants.size()==0;
                    int iter=100;
                    while(!a){
                        double x=r.nextInt(4);
                        if(x==0){
                            a=moveCohort(true, null);
                        }else if(x==1){
                            a= swapCohort(true, null);
                        }
                        else if(x==2){
                            a= moveStudent(true, null);
                        }else{
                            a=swapStudent(true, null);
                        }
                        if(a && !tt)break;
                        iter++;
                        if(iter>100)break;
                    }


                    //move student and swap student

                    if(calculateFitness()>=bestSementara){
                        phase = true;
                    }else{
                        bestSementara = calculateFitness();
                    }


//                    if (calculateFitness(jumlahStudent) >= bestSementara) indexStuck++;
//                    else {
//                        indexStuck = 0;
//                        bestSementara = calculateFitness(jumlahStudent);
//
//                    }
//
//                    if (indexStuck > 1000) {
//                        phase = true;
//                        indexStuck = 0;
//                    }
            }
            }


        }



        System.out.println("number iteration:"+iteasi);

        printMovement(movement);
        printBestMovement(bestMovement);
    }


    public static boolean swapStudent(boolean t, Student ss){
        int x=calculateFitness();
        Cohort c=null;



        Student s;


        if(ss==null){
            c = cohort.get(r.nextInt(cohort.size()));
            if(c.students.size()==0)return false;
            s=c.students.get(r.nextInt(c.students.size()));

        }else {
            s=ss;
            List<Cohort> listC=new ArrayList<>(cohort);

            Collections.sort(listC);

            for(int i=0;i<listC.size();i++){
                if(listC.get(i).students.contains(s)){
                    c=listC.get(i);
                    break;
                }
            }
            if(c==null)return false;

        }
        List<Cohort> listDestination=new ArrayList<>();

        for(int i=0;i<cohort.size();i++){
            if(cohort.get(i)!=c && cohort.get(i).timeSlot!=null){
                if(cohort.get(i).students.size()<cohort.get(i).timeSlot.room.kapasitas) {
                    if (s.cekAvail(cohort.get(i).timeSlot.timeSlotId)) {
                        boolean tt=false;
                        for(int j=0;j<cohort.get(i).students.size();j++){
                            if(cohort.get(i).students.get(j).cekAvail(c.timeSlot.timeSlotId)){
                                tt=true;
                                break;
                            }
                        }
                        if(tt)
                        listDestination.add(cohort.get(i));

                    }
                }
            }
        }

        if(listDestination.size()==0)return false;

        Cohort destination=listDestination.get(r.nextInt(listDestination.size()));

        List<Student> listStudentDestination=new ArrayList<>();
        for(int i=0;i<destination.students.size();i++){
            if(destination.students.get(i).cekAvail(c.timeSlot.timeSlotId)){
                listStudentDestination.add(destination.students.get(i));
            }
        }

        Student swap=listStudentDestination.get(r.nextInt(listStudentDestination.size()));

        c.students.remove(s);
        destination.students.add(s);

        destination.students.remove(swap);
        c.students.add(swap);

        int xy=calculateFitness();

        if(xy<x || !t){

            return true;
        }else{
            c.students.add(s);
            destination.students.remove(s);

            destination.students.add(swap);
            c.students.remove(swap);

            if(calculateFitness()!=x){
                System.exit(0);
            }
            return false;
        }


    }



    public static boolean moveStudent(boolean t, Student ss){
        int x=calculateFitness();
        Cohort c=null;



        Student s;


        if(ss==null){
            c = cohort.get(r.nextInt(cohort.size()));
            if(c.students.size()==0)return false;
            s=c.students.get(r.nextInt(c.students.size()));

        }else {
            s=ss;
            List<Cohort> listC=new ArrayList<>(cohort);

            Collections.sort(listC);

            for(int i=0;i<listC.size();i++){
                if(listC.get(i).students.contains(s)){
                    c=listC.get(i);
                    break;
                }
            }

            if(c==null)return false;

        }


        List<Cohort> listDestination=new ArrayList<>();

        for(int i=0;i<cohort.size();i++){
            if(cohort.get(i)!=c && cohort.get(i).timeSlot!=null){
                if(cohort.get(i).students.size()<cohort.get(i).timeSlot.room.kapasitas) {
                    if (s.cekAvail(cohort.get(i).timeSlot.timeSlotId)) {
                        listDestination.add(cohort.get(i));
                    }
                }
            }
        }

        if(listDestination.size()==0)return false;

        Cohort destination=listDestination.get(r.nextInt(listDestination.size()));

        if(destination.students.size()>=destination.timeSlot.room.kapasitas){
            System.out.println("Cohor:"+destination.id+" "+destination.students.size()+" "+destination.timeSlot.room.kapasitas);

            for(int i=0;i<cohort.size();i++){
                if(cohort.get(i)!=c && cohort.get(i).timeSlot!=null){
                    if(cohort.get(i).students.size()<cohort.get(i).timeSlot.room.kapasitas) {
                        if (s.cekAvail(cohort.get(i).timeSlot.timeSlotId)) {
                            if(cohort.get(i)==destination)System.out.println("lolos");
                        }
                    }
                }
            }
            System.exit(0);
        }

        c.students.remove(s);
        destination.students.add(s);

        int xy=calculateFitness();

       if(xy<x || !t){
           return true;
       }else{
           c.students.add(s);
           destination.students.remove(s);
           if(calculateFitness()!=x){
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
                moveCohort(false, null);
            } else {

                swapCohort(false, null);
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
                        a = moveCohort(true, null);
                    } else {
                        a = swapCohort(true, null);
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

    public static int calculateFitness(){


        int penalty=0;
        double averageStudent=jumlahStudent/6;
        double averageTeachingAssistance=(double) jumlahTeachingAssistance/6;
        int ideal=(int)Math.floor(averageStudent/averageTeachingAssistance);
        //System.out.println(averageStudent+" "+averageTeachingAssistance);




        for(int i=0;i<cohort.size();i++){
            if(cohort.get(i).timeSlot.timeSlotId%4==0){
                penalty+=cohort.get(i).students.size();
                penalty+=cohort.get(i).teachingAssitants.size();
            }
            if(cohort.get(i).timeSlot.timeSlotId%4==3){
                penalty+=cohort.get(i).students.size()*2;
                penalty+=cohort.get(i).teachingAssitants.size()*2;
            }

            int x=penalty;
            if(cohort.get(i).teachingAssitants.size()>0)
            if(cohort.get(i).students.size()>ideal*cohort.get(i).teachingAssitants.size()){
               // int distribute=cohort.get(i).students.size()-average>0?cohort.get(i).students.size()-average:average-cohort.get(i).students.size();

               // System.out.println(averageStudent+" "+averageTeachingAssistance+" "+ideal+" "+2*((cohort.get(i).students.size()/cohort.get(i).teachingAssitants.size())-ideal)+jumlahTeachingAssistance);
                //System.out.println(cohort.get(i).students.size()+" "+cohort.get(i).teachingAssitants.size()+" "+penalty);
                penalty+=(2*(cohort.get(i).students.size()-(ideal*cohort.get(i).teachingAssitants.size())));
                //System.out.println(penalty);
            }

            if(penalty<x)System.exit(0);
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

        if(!t) {
            for (int j = 0; j < cohort.size(); j++) {
                if (teachingAssitants.get(i).cekAvail(cohort.get(j).timeSlot.timeSlotId)) {
                    if (cohort.get(j).teachingAssitants.size() < 2) {

                        //if(cohort.get(j).cekTADouble(teachingAssitants.get(i).nama)) {

                        cohort.get(j).teachingAssitants.add(teachingAssitants.get(i));
                        teachingAssitants.remove(i);
                        return true;
                        // }
                    }
                }
            }
        }
        else{
           // System.out.println("kenna");
            for (int j = 0; j < cohort.size(); j++) {
                if (teachingAssitants.get(i).cekAvail(cohort.get(j).timeSlot.timeSlotId)) {


                        //if(cohort.get(j).cekTADouble(teachingAssitants.get(i).nama)) {

                        cohort.get(j).teachingAssitants.add(teachingAssitants.get(i));
                        teachingAssitants.remove(i);
                        return true;
                        // }
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

    public static boolean swapCohort(boolean t, Cohort cc) {
        boolean tt=student.size()==0 && teachingAssitants.size()==0?true:false;

        Cohort c ;
        if(cc==null){
            c= cohort.get(r.nextInt(cohort.size()));
        }else{
            c=cc;
        }



        List<TimeSlot> ts = new ArrayList<>();
        List<Integer> numberConflict = new ArrayList<>();

        for (int i = 0; i < cohort.size(); i++) {
            if (cohort.get(i) != c) {
                ts.add(cohort.get(i).timeSlot);

                int conflict = 0;

                conflict += c.tesKonfilk(cohort.get(i).timeSlot.timeSlotId);
                conflict += cohort.get(i).tesKonfilk(c.timeSlot.timeSlotId);

                if(c.students.size()>cohort.get(i).timeSlot.room.kapasitas){
                    conflict+=c.students.size()-cohort.get(i).timeSlot.room.kapasitas;
                }

                if(cohort.get(i).students.size()>c.timeSlot.room.kapasitas){
                    conflict+=cohort.get(i).students.size()-c.timeSlot.room.kapasitas;
                }

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
                if(tt)x=calculateFitness();

                TimeSlot terpilih = combined.get(r.nextInt(xx + 1)).getKey();
                TimeSlot terpilih2 = c.timeSlot;

                Cohort swap1 = terpilih.cohort;


                terpilih.cohort = c;
                terpilih2.cohort = swap1;

                c.timeSlot = terpilih;
                swap1.timeSlot = terpilih2;


//                if(tt)
//                if(calculateFitness(jumlahStudent)<=x){
//                    c.cekKesesuaian();
//                    swap1.cekKesesuaian();
//
//
//                    // System.out.println("berhasil");
//
//                    return true;
//                }else {
//                    terpilih.cohort=swap1;
//                    terpilih2.cohort=c;
//
//                    c.timeSlot=terpilih2;
//                    swap1.timeSlot=terpilih;
//
//
//
//                    return false;
//                }

                c.cekKesesuaian();
                swap1.cekKesesuaian();


                // System.out.println("berhasil");

                return true;


            } else {
                return false;
            }

        } else {
            //TimeSlot terpilih = combined.get((int) ((1 - probabilitasEksplorasi) * combined.size())).getKey();

            TimeSlot terpilih= combined.get(r.nextInt(combined.size())).getKey();


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

            while (c.students.size()>c.timeSlot.room.kapasitas){
              //  System.out.println("awal:"+c.students.size()+" "+Main.student.size());
                Student x=c.students.get(r.nextInt(c.students.size()));
                student.add(x);
                c.students.remove(x);
               // System.out.println("akhir:"+c.students.size()+" "+Main.student.size());
            }
            while (swap1.students.size()>swap1.timeSlot.room.kapasitas){
                //swap1.students.remove(r.nextInt(swap1.students.size()));

                Student x=swap1.students.get(r.nextInt(swap1.students.size()));
                student.add(x);
                swap1.students.remove(x);
            }

            return true;
        }


    }

    public static boolean moveCohort(boolean t, Cohort cc) {

        boolean tt=student.size()==0 && teachingAssitants.size()==0?true:false;


        Cohort c;

        if(cc==null){
            c= cohort.get(r.nextInt(cohort.size()));
        }else{
            c=cc;
        }


        List<TimeSlot> ts = new ArrayList<>();
        List<Integer> numberConflict = new ArrayList<>();

        for (int i = 0; i < timeSlot.size(); i++) {
            if (timeSlot.get(i).cohort == null) {

                if (timeSlot.get(i) == c.timeSlot) {
                    System.out.println("sama");
                    System.exit(0);
                }

                ts.add(timeSlot.get(i));
                int konflik=c.tesKonfilk(timeSlot.get(i).timeSlotId);
                if(c.students.size()>timeSlot.get(i).room.kapasitas){
                    konflik+=c.students.size()-timeSlot.get(i).room.kapasitas;
                }
                numberConflict.add(konflik);
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

                if(tt)prevPen=calculateFitness();

                c.timeSlot.cohort = null;
                c.timeSlot = terpilih;
                terpilih.cohort = c;

//                if(tt)
//                if(prevPen<calculateFitness(jumlahStudent)){
//                    c.timeSlot.cohort=null;
//                    c.timeSlot=awal;
//                    awal.cohort=c;
//
//                    return false;
//                }

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

            //TimeSlot terpilih = combined.get((int) ((1 - probabilitasEksplorasi) * combined.size())).getKey();

            TimeSlot terpilih= combined.get(r.nextInt(combined.size())).getKey();

            c.timeSlot.cohort = null;
            c.timeSlot = terpilih;
            terpilih.cohort = c;

            c.removeKonflik();
            c.cekKesesuaian();

            while (c.students.size()>c.timeSlot.room.kapasitas){

                Student x=c.students.get(r.nextInt(c.students.size()));
                student.add(x);
                c.students.remove(x);
            }

            if(c.students.size()>c.timeSlot.room.kapasitas)System.exit(0);

        }

        return false;
    }

    public static void printHasil(int data, int pen, double dur, int ver) throws IOException {


        myWriter = new FileWriter(path + "/Hasil/" + data+"-"+ver+"_readable.csv");
        Main.myWriter.write("Penalty:"+pen+", duration:"+dur+"\n\n");
        Main.myWriter.write("first feasible time:"+firstFeasibleTime+", best feasible time:"+bestFeasibleTime+"\n\n");
        for (int i = 0; i < cohort.size(); i++) {
            cohort.get(i).printHasil2();
        }
        myWriter.close();

    }

    public static void printHasil2(int data, int pen, double dur, int ver) throws IOException {


        myWriter = new FileWriter(path + "/Hasil/" + data+"-"+ver+".csv");
          for (int i = 0; i < cohort.size(); i++) {
            cohort.get(i).printHasil3();
        }
        myWriter.close();

    }


    public static void printMovement(List<String> data) throws IOException {


        myWriter = new FileWriter(path + "/Hasil/movement.csv");
        Main.myWriter.write("penalt;running time\n");

        for (int i = 0; i < data.size(); i+=1) {
            Main.myWriter.write(data.get(i)+"\n");
        }
        myWriter.close();

    }

    public static void printBestMovement(List<String> data) throws IOException {


        myWriter = new FileWriter(path + "/Hasil/bestMovement"+".csv");

        for (int i = 0; i < data.size(); i+=1) {
            Main.myWriter.write(data.get(i)+"\n");
        }
        myWriter.close();

    }

}

