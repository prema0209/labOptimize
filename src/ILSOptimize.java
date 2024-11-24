

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;




public class ILSOptimize {


    double thresholdValue;
     double minThresholdValue;
     double currentSoftConstraint, bestSoftConstraint;


     List<Double> listSelisih;


     List<Cohort> listCohort;
     List<Student> listStu;
     List<TeachingAssitant> listTa;




     int moveSelected;
     Random r;

     boolean firstRun;




    double initialSOl;


    public void optimize(){

        r=new Random();

        listCohort=new ArrayList<>(Main.cohort);
        listStu=new ArrayList<>(Main.student);
        listTa =new ArrayList<>(Main.teachingAssitants);


        Main.student=new ArrayList<>();
        Main.teachingAssitants=new ArrayList<>();


        currentSoftConstraint = Main.calculateFitness();
        bestSoftConstraint = currentSoftConstraint;
        Main.setBest();



        System.out.println("awal best:"+currentSoftConstraint);

        int iterasi = 0;
        Main.setRollback();


        // System.out.println("Solusi awal:" + );

        long startTime = System.currentTimeMillis(); // Get the start time
        long duration = 36000000; // Duration in milliseconds (1 hour = 3600000 ms)
        duration=14400000;
       // duration=360000;
        duration=36000000;
        duration = 600000;//10 menit
        //duration = 2100000;

        duration = 60000;//1 menit


        boolean phase = true;
        boolean improve = true;
        boolean movaAceeptancePhase = false;

        double newSol = currentSoftConstraint;

        double localBest = 0;
        double softAwal = 0;

        int peningkat=0;

        boolean calculateThreshold = true;
        boolean improveLocalBest=false;

        boolean oneCyle=false;

        calculateThreshold(currentSoftConstraint, true);

        System.out.println("solusiAwal:"+currentSoftConstraint);


        int it=0;



        while (!(System.currentTimeMillis() - startTime >= duration && oneCyle)) {

            if(bestSoftConstraint==0)break;

            iterasi++;



            if(iterasi%2000==0){
                System.out.println("------------current:"+currentSoftConstraint+" best:"+bestSoftConstraint+" local best:"+localBest+"list :"+listSelisih.size()+"----------------------");
                System.out.println(newSol+" "+thresholdValue+" "+softAwal);
                System.gc();
            }





            if (phase) {

                    Main.moveCohort(false, null);


                       newSol = Main.calculateFitness();



                       if(newSol>initialSOl){
                           initialSOl=newSol;

                       }


                       if(newSol>currentSoftConstraint+thresholdValue || newSol>=initialSOl) {
                           phase = false;
                           localBest = newSol;
                           Main.setCheckPoint();
                           improve = true;
                           softAwal = newSol;
                           peningkat = 1;
                           improveLocalBest = false;

                           Main.setRollback();

                       }



            } else {

                if(listSelisih.size()>0){
                    if(softAwal-listSelisih.get(0)>newSol){
                        softAwal=softAwal-listSelisih.get(0);
                    }
                    else{
                        softAwal = newSol;
                    }

                }else{
                    softAwal = newSol;
                }



                if (!improve) {
                    softAwal += thresholdValue;
                }
                improve = false;


                Collections.shuffle(listStu);
                Collections.shuffle(listTa);
                Collections.shuffle(listCohort);


                for(int i=0;i<listCohort.size();i++){

                    if(Math.random()>0.5){
                        Main.moveCohort(false, listCohort.get(i));
                    }else{
                        Main.swapCohort(false, listCohort.get(i));
                    }

                    double newSoft = Main.calculateFitness();

                    if (newSoft < softAwal || (newSoft == softAwal && newSol == softAwal)) {

                       Main.setRollback();



                        newSol = newSoft;

                        if (newSol < softAwal) {
                            improve = true;
                            //berkaitan dgn gap diatas
                        }

                        if (newSol < localBest && Main.cekFeasible()) {
                            localBest = newSol;
                            Main.setCheckPoint();
                            improveLocalBest = true;
                        }
                    }else{
                        Main.useRollback();

                        if(Main.calculateFitness()!=newSol){
                            System.out.println("Beda");
                            System.out.println(Main.calculateFitness()+" "+newSol);
                            System.exit(0);
                        }
                        else{
                           // System.out.println("sama");
                        }
                    }
                }

                for(int i=0;i<Main.student.size();i++){

                    Main.addStudent(i);

                    double newSoft = Main.calculateFitness();

                    if (newSoft < softAwal || (newSoft == softAwal && newSol == softAwal)) {

                        Main.setRollback();



                        newSol = newSoft;

                        if (newSol < softAwal) {
                            improve = true;
                            //berkaitan dgn gap diatas
                        }

                        if (newSol < localBest && Main.cekFeasible()) {
                            localBest = newSol;
                            Main.setCheckPoint();
                            improveLocalBest = true;
                        }
                    }else{
                        Main.useRollback();
                        if(Main.calculateFitness()!=newSol){
                            System.out.println("Beda");
                            System.out.println(Main.calculateFitness()+"-"+newSol);
                            System.exit(0);
                        }
                        else{
                            // System.out.println("sama");
                        }
                    }
                }

                for(int i=0;i<Main.teachingAssitants.size();i++){

                    Main.addTeachingAssitant(i);

                    double newSoft = Main.calculateFitness();

                    if (newSoft < softAwal || (newSoft == softAwal && newSol == softAwal)) {

                        Main.setRollback();



                        newSol = newSoft;

                        if (newSol < softAwal) {
                            improve = true;
                            //berkaitan dgn gap diatas
                        }

                        if (newSol < localBest && Main.cekFeasible()) {
                            localBest = newSol;
                            Main.setCheckPoint();
                            improveLocalBest = true;
                        }
                    }else{
                        Main.useRollback();
                        if(Main.calculateFitness()!=newSol){
                            System.out.println("Beda");
                            System.out.println(Main.calculateFitness()+"-"+newSol);
                            System.exit(0);
                        }
                        else{
                            // System.out.println("sama");
                        }
                    }
                }




                for(int i=0;i<listStu.size();i++){

                    if(listStu.get(i)==null){
                        System.out.println(listStu.get(i));
                    }
                    if(Math.random()>0.5){
                        Main.moveStudent(false, listStu.get(i));
                    }else{
                        Main.swapStudent(false, listStu.get(i));
                    }

                    double newSoft = Main.calculateFitness();

                    if (newSoft < softAwal || (newSoft == softAwal && newSol == softAwal)) {

                        Main.setRollback();



                        newSol = newSoft;

                        if (newSol < softAwal) {
                            improve = true;
                            //berkaitan dgn gap diatas
                        }

                        if (newSol < localBest && Main.cekFeasible()) {
                            localBest = newSol;
                            Main.setCheckPoint();
                            improveLocalBest = true;
                        }
                    }else{
                        Main.useRollback();
                        if(Main.calculateFitness()!=newSol){
                            System.out.println("Beda");
                            System.out.println(Main.calculateFitness()+"-"+newSol);
                            System.exit(0);
                        }
                        else{
                           // System.out.println("sama");
                        }
                    }
                }


                if (!improve) {
                   // System.out.println("masuk sini");

                    if (listSelisih.size() == 0) {
                        movaAceeptancePhase = true;
                           if (localBest < bestSoftConstraint) {
                          //  System.out.println("lebih kecil local best:..............................................................");
                        }
                    } else {
                        //  System.out.println(improveLocalBest);
                        if(improveLocalBest){
                            peningkat=1;
                            improveLocalBest=false;

                        }else{
                            peningkat++;
                        }




                        for(int k=0;k<peningkat;k++) {
                            if(listSelisih.size()==0)break;
                            listSelisih.remove(listSelisih.size() - 1);
                        }

                        //  System.out.println(listSelisih.size()+" "+peningkat);


                        double sel=listSelisih.size()>3?listSelisih.get(r.nextInt(listSelisih.size() / 4)):0;

                        if (newSol > localBest + sel) {
                            Main.useCheckPoint();
                            Main.setRollback();
                           // Main.calulateSoftConstraint(true);
                            //Main.calulateSoftConstraint(true);
                            newSol=localBest;
                            //  System.out.println("balik ke local best:"+Main.calculateFitness()+" "+newSol);
                        }
                    }


                    if (listSelisih.size() > 1) {
                        calculateAverageThreshold();
                    } else {
                        thresholdValue = 0;
                    }
//                    if(newSol!=Main.calculateSoftConstraint()){
//                        System.out.println("ga sesuai 1");
//                        System.exit(0);
//                    }

                }else{
                   // System.out.println("improve: "+newSol);
                }
            }


            if (movaAceeptancePhase) {

                it++;
               // System.out.println("iterasi:"+it);
                oneCyle=true;

                double newSoft = Main.calculateFitness();

                if (newSol != newSoft) {
                    System.out.println("1:" + newSol + " " + newSoft);
                    System.exit(0);
                }


                // System.out.println("masuk move aceptance phase:" + newSol + " best:" + bestSoftConstraint);

                //use local best


                Main.useCheckPoint();
                Main.setRollback();

//                Main.calulateSoftConstraint(true);



                newSol = localBest;

                if(localBest!=Main.calculateFitness()){
                    System.exit(0);
                }




//                newSoft=Main.calculateSoftConstraint();
//
//
//                if(newSol!=localBest || localBest!=newSoft){
//                    System.out.println("2:"+localBest+" "+newSol+" "+newSoft);
//                    System.exit(0);
//                }


                double xx = newSol;


                //hill climbing improvement
//                if (true) {
//
//                    boolean st = true;
//
//
//                    while (st) {
//                        st = false;
//                        Collections.shuffle(listClass);
//                        Collections.shuffle(listStu);
//
//
//                        for (int i = 0; i < listClass.size(); i++) {
//
//
//                            int ts = r.nextInt(Main.timeSlot.length);
//
//                            while (ts == Main.exams.get(i).timeSlot) {
//                                ts = r.nextInt(Main.timeSlot.length);
//                            }
//
//                            DataLLH llh2 = new DataLLH(ts, listClass.get(i), false);
//                            if(llh2.feasible) {
//
//                                newSoft = llh2.softConstraint;
//                                if (newSoft < newSol) { //bisa juga sama dengan namun st dipindah
//                                    newSol = newSoft;
//                                    llh2.acceptLLH();
//                                    st = true;
//                                }
//                        }
//
//                        }
//
//
//
//                    }
//                    localBest = newSol;
//                    Main.setCheckPoint();
//
//                }





              //  System.out.println("Move Acceptance phase:" + newSol + " curr soft:" + currentSoftConstraint+" best:"+bestSoftConstraint);




                calculateThreshold(newSol, true);

                newSoft = Main.calculateFitness();

                if (newSol != newSoft) {
                    System.out.println("1:" + newSol + " " + newSoft);
                    System.exit(0);
                }




                if (moveAcceptance(currentSoftConstraint, newSol)) {
                    currentSoftConstraint = Main.calculateFitness();
                  //  System.out.println("penalty: " + currentSoftConstraint + " best:" + bestSoftConstraint + " ts:" + thresholdValue + " mints:" + minThresholdValue);


                } else {
                    if (localBest < bestSoftConstraint + listSelisih.get(r.nextInt(listSelisih.size() / 4))) {

                        currentSoftConstraint = Main.calculateFitness();
                      //  System.out.println("penalty2: " + currentSoftConstraint + " best:" + bestSoftConstraint + " ts:" + thresholdValue + " mints:" + minThresholdValue);
                    } else {
                    //    System.out.println("gagal:" + newSol+" "+( listSelisih.get(r.nextInt(listSelisih.size() / 4))));
                        ;
                        Main.useBest();

                        //Main.calulateSoftConstraint(true);
                        currentSoftConstraint = Main.calculateFitness();
                     //   System.out.println("cur soft:"+currentSoftConstraint);
//                        if(currentSoftConstraint!=Main.calculateSoftConstraint()){
//                            System.exit(0);
//                        }

                    }



                }



                movaAceeptancePhase = false;
                phase = true;

              //  System.out.println("panjang list:" + listSelisih.size());

//                System.out.println(it+";"+ currentSoftConstraint);
//
//                System.out.println("Solusi saat ini:"+Main.calculateFitness());

//                if(thresholdValue>100){
//                    System.out.println(thresholdValue);
//                    for(int k=0;k<listSelisih.size();k++){
//                        System.out.println(listSelisih.get(k));
//                    }
//                }


            }
        }


        if(localBest>bestSoftConstraint) {
            Main.useBest();

        }else{
            Main.useCheckPoint();
        }

        currentSoftConstraint=Main.calculateFitness();
        System.out.println("hasil :"+currentSoftConstraint);

    }

    public boolean moveAcceptance(double penaltyAwal, double penaltyAkhir) {


       // System.out.println("move accc:"+penaltyAkhir+" "+bestSoftConstraint+" "+Main.calculateFitness());


        if(Main.calculateFitness()<bestSoftConstraint){
            System.out.println("move accc:"+penaltyAkhir+" "+bestSoftConstraint+" "+Main.calculateFitness());
        }

        if (penaltyAkhir <= bestSoftConstraint) {
            if(Main.cekFeasible()) {
                bestSoftConstraint = penaltyAkhir;
                Main.setBest();
            }
        }

        if (penaltyAwal < penaltyAkhir) return false;

        return true;

    }



    public void calculateAverageThreshold() {
        double x = 0;


        for (int i = 0; i < listSelisih.size(); i++) {
            x += listSelisih.get(i);
        }

        thresholdValue = x / listSelisih.size();

        minThresholdValue = listSelisih.get(0);

    }

    public void calculateThreshold(double curentPenalty, boolean t) {

        //tunningThresholdValue=20;
        listSelisih = new ArrayList<>();
        minThresholdValue = curentPenalty;



        Collections.shuffle(listCohort);Collections.shuffle(listStu);

       while(listSelisih.size()<=0) {

           for (int i = 0; i < listCohort.size(); i++) {
              // System.out.println("class"+i+" list:"+listSelisih.size());


               if(Math.random()>0.5){
                   Main.moveCohort(false, listCohort.get(i));
               }else{
                   Main.swapCohort(false, listCohort.get(i));
               }

                   double newPen = Main.calculateFitness();


                       if (newPen > initialSOl) {
                           initialSOl = newPen;
                           //System.out.println("new worstSol:" + initialSOl);
                       }


                   if (newPen != curentPenalty) {

                       double selisih = curentPenalty - newPen;
                       if (selisih < 0) selisih *= -1;

                       listSelisih.add(selisih);

                   }

                   Main.useRollback();

           }

           for(int i=0;i<listStu.size();i++){

               if(Math.random()>0.5){
                   Main.moveStudent(false, listStu.get(i) );
               }else{
                   Main.swapStudent(false, listStu.get(i));
               }




               double newPen = Main.calculateFitness();


               if (newPen > initialSOl) {
                   initialSOl = newPen;
                   //System.out.println("new worstSol:" + initialSOl);
               }


               if (newPen != curentPenalty) {

                   double selisih = curentPenalty - newPen;
                   if (selisih < 0) selisih *= -1;

                   listSelisih.add(selisih);

               }

               Main.useRollback();

           }


       }

        Collections.sort(listSelisih);
        calculateAverageThreshold();



       // System.out.println("panjang:"+listSelisih.size()+" "+thresholdValue);



//
//        double perbandingan = (minThresholdValue * 100 / thresholdValue);
//
//        System.out.println("threshold :" + thresholdValue + " min selisih:" + minThresholdValue + " perbandingn:" + perbandingan + "% loop yg dibutuhkan:" + (100 / perbandingan));
//
//        System.out.println(Main.exams.size() + " " + listSelisih.size());

//
//        for(double x :listSelisih){
//            System.out.println(x);
//        }
//
//
//
//        System.exit(0);

    }






}
