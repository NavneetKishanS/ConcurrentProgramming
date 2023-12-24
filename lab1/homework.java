import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class homework
{
    private static volatile long totalSum = 0;

    public static class AddNums extends Thread{
        //private PrintWriter pw;
        //private int num;
        //public AddNums(){}
        //public addNums(PrintWriter pw, int num){ this.pw = pw; this.num = num;}
        private int start;
        private int end;

        public AddNums(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public void run(){
            long startTime = System.nanoTime();
            int count = 0;
            //long totalSum;
            long localSum =0;
            for(int i = start;i<= end;i++){
                localSum += i;
            }
            totalSum += localSum;
            // for (int i = start; i <= 1_000_000_000; i++)
            // {
            //     //count += i;
            //     System.out.println(count);
            //     //pw.println(count);
            // }
            // long endTime = System.nanoTime();
            // long Duration = endTime - startTime;
            // System.out.println("Sum: " + count);
            // System.out.println("Time taken: " + Duration); 

            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            double seconds = duration / 1_000_000_000.0;

            System.out.println("Thread " + Thread.currentThread().getName() + " Sum: " + localSum + " Time taken: " + seconds + " seconds");
        }

    }
    public static void main(String[] args){
        // Thread t = new addNums();
        // t.start();

        final int THREAD_COUNT =10;
        final int RANGE = 100_000_000;
        Thread[] threads = new Thread[THREAD_COUNT];

        for(int i =0;i<THREAD_COUNT ;i++){
            int start = i*RANGE + 1;
            int end = (i+1)*RANGE;
            threads[i] = new AddNums(start, end);
            threads[i].start();
        }

        for(Thread thread : threads){
            try{
                thread.join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        System.out.println("Total Sum: " + totalSum);
    }
}