public class TimeSlot {



    int timeSlotId;
    Room room;
    Cohort cohort;


    public TimeSlot(int i,Room x ){

        room=x;
        timeSlotId=i;
    }

    public void praproses(){
        boolean t=false;
        for(int i=0;i<Main.student.size();i++){
            if(Main.student.get(i).avail[timeSlotId]){
                t=true;
                break;
            }
        }

        if(!t)System.out.println("kena");
    }

}
