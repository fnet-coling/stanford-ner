import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Generics;

import java.io.*;
import java.util.List;

/**
 * Created by mayk on 6/30/14.
 */
public class RRTest {

    public static List<String> readFile(String file) throws IOException {
        BufferedReader rd = new BufferedReader( new FileReader(file));

        int count = 0;

        String line;
        List<String> ls = Generics.newLinkedList();
        while((line= rd.readLine())!=null){

//            String line2= rd.readLine();
//            String[] splitedLabel = line.split("\t");
//            if(splitedLabel.length<1||splitedLabel[0].isEmpty())
//                break;
//            String line3 = rd.readLine();
//            String[] splitedWords =  line3.split("\t");
//            StringBuilder sb = new StringBuilder();
//            for(String s: splitedWords){
//                if(!s.isEmpty()){
//                sb.append(s);
//                sb.append(" ");
//                }
//                else
//                    break;
//            }
            ls.add(line.toLowerCase().trim());
          //  rd.readLine();

//            System.out.println(line3);
        }
        return ls;
    }
    public static String extract(String result){

        String[] results =result.split(" ");
        StringBuilder sb = new StringBuilder();
        boolean begin = false;
        for(String r : results){
            String[] splited = r.split("/");
            if(splited.length<2) continue;

            if(!splited[1].equals("O"))
            {
                if(!begin)
                    begin = true;
                sb.append(splited[0]);
                sb.append(" ");
            }
            else
            {
                if(begin)
                {
                   System.out.println(sb.toString().trim());
                   sb.setLength(0);
                   begin = false;
                }
            }
        }
        if(sb.toString().trim().length()!=0)
        System.out.println(sb.toString().trim());
        return null;
    }
    public static void main(String[] args) throws Exception {
         List<String> ls =  readFile("./test");
        String serializedClassifier = "classifiers/english.muc.7class.caseless.distsim.crf.ser.gz";
        Writer wr = new FileWriter("./hypo");
        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
      //  extract(classifier.classifyToString("SO AND IT WAS LIKE THEY WERE THEY'RE PUERTO RICAN AND WERE AMERICAN".toLowerCase()));
        Integer i = 0;
        for(String s : ls){
           wr.write(classifier.classifyToString(s)+"\n");
            i+=1;
        }



        //extract(classifier.classifyToString("this is Smith"));



    }
}

