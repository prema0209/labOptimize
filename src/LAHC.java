

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class LAHC {


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



    double[] lahcList;
    double initialSOl;
    List<String> catatan;


    public void optimize(){

        r=new Random();
        catatan=new ArrayList<>();

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

        //duration = 60000;//1 menit


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



        System.out.println("solusiAwal:"+currentSoftConstraint);


        int it=0;


        lahcList = new double[5000];

        for (int i = 0; i < lahcList.length; i++) {
            lahcList[i] = currentSoftConstraint*10;
        }


        while (!(System.currentTimeMillis() - startTime >= duration )) {

            if(bestSoftConstraint==0)break;

            iterasi++;


            Main.setRollback();



            if(iterasi%2000==0){
                System.out.println("------------current:"+currentSoftConstraint+" best:"+bestSoftConstraint+" local best:"+localBest+"");
                System.out.println(newSol+" "+thresholdValue+" "+softAwal);
                System.gc();
            }


            localBest=currentSoftConstraint;
            Main.setCheckPoint();



       //     if(Math.random()<0.5) {

                for (int i = 0; i < listCohort.size(); i++) {

                    if (Math.random() > 0.5) {
                        Main.moveCohort(false, listCohort.get(i));
                    } else {
                        Main.swapCohort(false, listCohort.get(i));
                    }

//                    if (Main.cekFeasible()) {
//                        int sof = Main.calculateFitness();
//                        if (currentSoftConstraint > sof) {
//                            localBest = sof;
//                            Main.setCheckPoint();
//                        }
//                    }

                    double newSoft=Main.calculateFitness();
                    if(moveAcceptance(currentSoftConstraint,newSoft, lahcList[iterasi % lahcList.length])){


                        currentSoftConstraint=Main.calculateFitness();
                        Main.setRollback();
                        if(Main.cekFeasible())
                        if(bestSoftConstraint>currentSoftConstraint){
                            bestSoftConstraint=currentSoftConstraint;
                            Main.setBest();
                        }

                    }else{
                        Main.useRollback();
                    }




                    if(Main.cekFeasible())
                    lahcList[iterasi % lahcList.length] = currentSoftConstraint;





                }


                for (int i = 0; i < Main.teachingAssitants.size(); i++) {

                    Main.addTeachingAssitant(i);
//
//                    if (Main.cekFeasible()) {
//                        int sof = Main.calculateFitness();
//                        if (currentSoftConstraint > sof) {
//                            localBest = sof;
//                            Main.setCheckPoint();
//                        }
//                    }
//

                    double newSoft=Main.calculateFitness();
                    if(moveAcceptance(currentSoftConstraint,newSoft, lahcList[iterasi % lahcList.length])){


                        currentSoftConstraint=Main.calculateFitness();
                        Main.setRollback();
                        if(Main.cekFeasible())
                        if(bestSoftConstraint>currentSoftConstraint){
                            bestSoftConstraint=currentSoftConstraint;
                            Main.setBest();
                        }

                    }else{
                        Main.useRollback();
                    }


                    if(Main.cekFeasible())
                        lahcList[iterasi % lahcList.length] = currentSoftConstraint;


                }

                for (int i = 0; i < Main.student.size(); i++) {

                    Main.addStudent(i);

//                    if (Main.cekFeasible()) {
//                        int sof = Main.calculateFitness();
//                        if (currentSoftConstraint > sof) {
//                            localBest = sof;
//                            Main.setCheckPoint();
//                        }
//                    }

                    double newSoft=Main.calculateFitness();
                    if(moveAcceptance(currentSoftConstraint,newSoft, lahcList[iterasi % lahcList.length])){


                        currentSoftConstraint=Main.calculateFitness();
                        Main.setRollback();
                        if(Main.cekFeasible())
                        if(bestSoftConstraint>currentSoftConstraint){
                            bestSoftConstraint=currentSoftConstraint;
                            Main.setBest();
                        }

                    }else{
                        Main.useRollback();
                    }


                    if(Main.cekFeasible())
                        lahcList[iterasi % lahcList.length] = currentSoftConstraint;

                }

            //}

            for(int i=0;i<listStu.size();i++){

                if(listStu.get(i)==null){
                    System.out.println(listStu.get(i));
                }

                if(Math.random()>0.5){
                    Main.moveStudent(false, listStu.get(i));
                }else{
                    Main.swapStudent(false, listStu.get(i));
                }

//                if(Main.cekFeasible()){
//                    int sof=Main.calculateFitness();
//                    if(currentSoftConstraint>sof){
//                        localBest=sof;
//                        Main.setCheckPoint();
//                    }
//                }


                double newSoft=Main.calculateFitness();
                if(moveAcceptance(currentSoftConstraint,newSoft, lahcList[iterasi % lahcList.length])){


                    currentSoftConstraint=Main.calculateFitness();
                    Main.setRollback();

                    if(Main.cekFeasible())
                    if(bestSoftConstraint>currentSoftConstraint){
                        bestSoftConstraint=currentSoftConstraint;
                        Main.setBest();
                    }

                }else{
                    Main.useRollback();
                }


                if(Main.cekFeasible())
                    lahcList[iterasi % lahcList.length] = currentSoftConstraint;



            }



       //     Main.useCheckPoint();



//            if(moveAcceptance(currentSoftConstraint,newSoft, lahcList[iterasi % lahcList.length])){
//
//
//                currentSoftConstraint=Main.calculateFitness();
//                Main.setRollback();
//
//                if(bestSoftConstraint>currentSoftConstraint){
//                    bestSoftConstraint=currentSoftConstraint;
//                    Main.setBest();
//                }
//
//            }else{
//                Main.useRollback();
//            }
//
//
//
//
//            lahcList[iterasi % lahcList.length] = currentSoftConstraint;
//
//





            //if(catatan.size()>200)break;
        }


        if(localBest>bestSoftConstraint) {
            Main.useBest();

        }else{
            Main.useCheckPoint();
        }

        currentSoftConstraint=Main.calculateFitness();
        System.out.println("hasil :"+currentSoftConstraint);

        for(int i=0;i<catatan.size();i++){
            System.out.println(catatan.get(i));
        }
        System.out.println(catatan.size());

    }


    public boolean moveAcceptance(double penalty, double newPenalty, double thresholdValue){


        if (penalty >= newPenalty || thresholdValue >= newPenalty) {

            return true;


        }

        return false;


    }










}
