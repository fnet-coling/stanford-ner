import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import sun.print.resources.serviceui_it;

import java.io.*;
import java.util.*;

/**
 * Created by mayk on 7/23/14.
 */
public class IntToText {
    public static List<String> readDict(String file) throws IOException {
        BufferedReader rd = new BufferedReader( new FileReader(file));

        int count = 0;

        String line;
        List<String> ls = Generics.newArrayList(20000);
        while((line= rd.readLine())!=null){
            String[] splited = line.split(" ");
            ls.add(splited[0]);
        }
        return ls;
    }

    public static void align(String f1, String f2, String delim, int field1, int field2) throws IOException {
        BufferedReader rd1 = new BufferedReader(new FileReader(new File(f1)));
        BufferedReader rd2 = new BufferedReader(new FileReader(new File(f2)));
        BufferedReader rd3 = new BufferedReader(new FileReader(new File("./thyp")));

        Map<String,String> map =new HashMap<String,String>();
        Map<String,String> hmap =new HashMap<String,String>();
        FileWriter wr = new FileWriter(new File("./align"));
        String line = null;
        while((line = rd3.readLine())!=null){
            String[] splited = line.split(delim);
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<splited.length;++i){
                if(i!=field1)
                {
                   sb.append(splited[i]);
                   sb.append(" ");
                }
            }
            hmap.put(splited[field1].toLowerCase(),sb.toString().trim());
        }
        while((line = rd1.readLine())!=null){
            String[] splited = line.split(delim);
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<splited.length;++i){
                if(i!=field1)
                {
                    sb.append(splited[i]);
                    sb.append(" ");
                }
            }
            map.put(splited[field1].toLowerCase(),sb.toString().trim());
        }
        while((line = rd2.readLine())!=null){
            String[] splited = line.split(delim);
            StringBuilder sb = new StringBuilder();
            for(int i = 0;i<splited.length;++i){
                if(i!=field2)
                {
                    sb.append(splited[i].replace("<eps>:1#<end>", "").replace("<end>", "\n"));
                    //sb.append("\n");
                }

            }

            String other = map.get(splited[field2]);
            String hother = hmap.get(splited[field2]);
            wr.write(splited[field2].toLowerCase() + " " + other + "\nREF:"+hother+"\n"+sb.toString().trim() + "\n");

        }
        wr.close();
    }

    public static List<String> readFile(String file,List<String>dict) throws IOException {
        BufferedReader rd = new BufferedReader( new FileReader(file));
        Writer wr = new FileWriter("./wcn");

        int count = 0  ;
        String line;
        List<String> ls = Generics.newLinkedList();
        while((line= rd.readLine())!=null){
            String[] splited = line.split("\\s");
            int i =1;
            wr.write(splited[0].toLowerCase() + " ");
            while(i<splited.length){
                if(splited[i].equals("[")) {
                    i++; continue;}
                else if (splited[i].equals("]")){
                    wr.write("<end>");i++; continue;}

            //    wr.write(dict.get(Integer.parseInt(splited[i])) + ":" + splited[i + 1] + "#");
                i+=2;
            }

            wr.write("\n");
        }

        return ls;
    }
    public  List<String> statistics(String file,List<String>dict) throws IOException {
        BufferedReader rd = new BufferedReader( new FileReader(file));
        Writer wr = new FileWriter("./statistics");

        int count = 0  ;
        String line;
        List<String> ls = Generics.newLinkedList();
        Map<String,Map<String,Double>> hmap = Generics.newHashMap();
        while((line= rd.readLine())!=null){
            System.out.println(count++);
            String[] splited = line.split("\\s");
            int i =1;
            List<Pair<String,Double>> strs = Generics.newArrayList();
            while(i<splited.length){
                if(splited[i].equals("[")) {
                    strs = Generics.newArrayList();
                    i++; continue;}
                else if (splited[i].equals("]")){
                    if(strs.size()!=0){
                    Map<String,Double> set = hmap.get(strs.get(0).first);
                    if(set==null){
                        set = Generics.newHashMap();
                        hmap.put(strs.get(0).first,set);}
                    for(Pair<String,Double> s:strs){
                        Double val = set.get(s.first);
                        if(val==null) val= 0.0;
                        set.put(s.first,val+s.second);
                    }}
                    i++;continue;}

                if(!splited[i].equals("0"))
                strs.add(Generics.newPair(dict.get(Integer.parseInt(splited[i])), Double.parseDouble(splited[i + 1])));
                //    wr.write(dict.get(Integer.parseInt(splited[i])) + ":" + splited[i + 1] + "#");
                i+=2;
            }
           // for(int k = 0;k<strs.size();++k){

           // }
        }

        for(Map.Entry<String,Map<String,Double>> entry: hmap.entrySet()){
            ValueComparator bvc =  new ValueComparator(entry.getValue());
            TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
            wr.write(entry.getKey() + ":n");
            sorted_map.putAll(entry.getValue());
            for(Map.Entry<String,Double> s : sorted_map.entrySet())
               wr.write("\t" + s + "\n");
        }
        wr.close();
        return ls;
    }
     class ValueComparator implements Comparator<String> {

        Map<String, Double> base;
        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
    public static void main(String[] args) throws IOException {
        new IntToText().statistics("/home/mayk/programming/data/newdata/2.sau", readDict("/home/mayk/programming/data/newdata/words.txt"));
        //align("tojoin","./wcn" , " ", 0,0 );
    }
}
