
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GetDataset {
    public void readRoom(String path){

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            int index1=0;
            while ((line = br.readLine()) != null) {

                if(index1>0){
                    String[] values = line.split(";"); // Asumsi bahwa pemisah antar kolom adalah koma
                    boolean av[]=new boolean[20];
                    for(int i=4;i<values.length;i++){
                        av[i-4]=values[i].equals("1")?true:false;
                    }

                    Main.room.add(new Room(values[1],values[2],Integer.parseInt(values[3]),av));

                }
                index1++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void readStudent(String path){

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            int index1=0;
            while ((line = br.readLine()) != null) {

                if(index1>0){
                    String[] values = line.split(";"); // Asumsi bahwa pemisah antar kolom adalah koma
                    boolean av[]=new boolean[20];
                    for(int i=4;i<values.length;i++){
                        av[i-4]=values[i].equals("1")?true:false;
                    }

                    Main.student.add(new Student(values[1],values[2],values[3],av));

                }
                index1++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void readTeachingAssistant(String path){

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            int index1=0;
            while ((line = br.readLine()) != null) {

                if(index1>0){
                    String[] values = line.split(";"); // Asumsi bahwa pemisah antar kolom adalah koma
                    boolean av[]=new boolean[20];
                    for(int i=5;i<values.length;i++){
                        av[i-5]=values[i].equals("1")?true:false;
                    }

                    Main.teachingAssitants.add(new TeachingAssitant(values[1],values[2],values[3],values[4],av));

                }
                index1++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void readName(String path){

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));

            int index1=0;
            while ((line = br.readLine()) != null) {


                    //String[] values = line.split(""); // Asumsi bahwa pemisah antar kolom adalah koma


                String nama=line.substring(0);
                //System.out.println(nama);
                   for(int i=0;i<Main.student.size();i++){
                       if(Main.student.get(i).nama.equals(nama)){
                           System.out.println(nama+"-"+Main.student.get(i).kelas+"-"+Main.student.get(i).id);
                       }
                   }





            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
