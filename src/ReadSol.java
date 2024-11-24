import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReadSol {



    public void readSOl(String filePath) {

        Random r=new Random();
        int x=r.nextInt(10);

        x++;

        x=1;

        String path=filePath+"-"+x+".csv";





        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            // Skip the header


            // Read each row
            while ((line = reader.readLine()) != null) {
                // Parse the row into a ClassSchedule object

                String[] values = line.split(",");

                int idCohort=Integer.parseInt(values[0]);


                Cohort c=null;
                for(int i=0;i<Main.cohort.size();i++){
                    if(idCohort==Main.cohort.get(i).id){
                        c=Main.cohort.get(i);
                        break;
                    }
                }



//                if(Integer.parseInt(values[1])!=c.id) {
//                    System.out.println("salah");
//                }


                String[] timeslotRoom=values[1].split(";");


                TimeSlot t=null;

                for(int i=0;i<Main.timeSlot.size();i++){
                    if(Integer.parseInt(timeslotRoom[0])==Main.timeSlot.get(i).timeSlotId && timeslotRoom[1].equals(Main.timeSlot.get(i).room.id)){
                        t=Main.timeSlot.get(i);
                        break;
                    }
                }

                c.timeSlot=t;
                t.cohort=c;


                String daftarTa[]=values[2].split(";");

                List<TeachingAssitant> ta=new ArrayList<>();


                for(int i=0;i<daftarTa.length;i++){
                    for(int j=0;j<Main.teachingAssitants.size();j++){
                        if(Main.teachingAssitants.get(j).id.equals(daftarTa[i])){
                            ta.add(Main.teachingAssitants.get(j));
                            break;
                        }
                    }
                }

                for(int i=0;i<ta.size();i++){
                    c.teachingAssitants.add(ta.get(i));

                }


                String daftarStu[]=values[3].split(";");
                List<Student> stu=new ArrayList<>();

                for(int i=0;i<daftarStu.length;i++){
                    for(int j=0;j<Main.student.size();j++){
                        if(Main.student.get(j).id.equals(daftarStu[i])){
                            stu.add(Main.student.get(j));
                            break;
                        }
                    }
                }


                for(int i=0;i<stu.size();i++){
                    c.students.add(stu.get(i));
                }










            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
