import java.util.*;

public class SyncList
{
    public static void main(String[] args){
        List<Integer>  list = new ArrayList<Integer>();
        Thread[] threads = new Thread[2];
        for(int i =0; i< 2;i++){
            int finali = i;
            threads[i] = new Thread(()->{
                     //SyncList.class //threads //args
                    for(int k= 1-finali;k<1_000_000;k+= 2){
                        synchronized(list){  
                        if(k!=0 && (list.size() ==0 || list.get(list.size()-1) != k-1)){ k -= 2; continue;}
                        list.add(k);
                    }
                }
            });
        }

        for(int i=0;i<2;i++) threads[i].start();

        try{
            for(int i=0;i<2;i++) threads[i].join();
        }catch(InterruptedException e){}

        System.out.println("List Size: " + list.size());
        for(int i =0;i<1000;i++) System.out.println(" " + list.get(i));
    }
}