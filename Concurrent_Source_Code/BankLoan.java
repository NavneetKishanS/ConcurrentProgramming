import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;

public class BankLoan
{
    //static int bankLoans = 0;
    public static void main(String[] args)
    {
        final int n = 10;
        //(() -> { while (!exited) { if (hasTask()) runTask(); else sleep(1); } })
        ExecutorService es = Executors.newFixedThreadPool(n);
        int clientLoans[] = new int[n];
        //int bankLoans = 0;
        //int bankLoans[] = new int[1];
        AtomicInteger bankLoans = new AtomicInteger(0);
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int finali = i;
            futures.add(es.submit(()->{
                int total = 0;
                for (int j = 0; j < 10000; j++) {
                    int loanAmount = ThreadLocalRandom.current().nextInt(100, 1000+1);
                    //clientLoans[finali] += loanAmount;
                    total += loanAmount;
                    //synchronized (es) {
                    //    bankLoans += loanAmount;
                    //}
                    bankLoans.addAndGet(loanAmount);
                }
                return total;
            }));
        }
        for (int i = 0; i < n; i++) {
            try {
                clientLoans[i] = futures.get(i).get();
            } catch (InterruptedException|ExecutionException e) {}
        }
        es.shutdown(); //exited=true
        try {
            es.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
        System.out.println(bankLoans);
        for (int i = 0; i < n; i++) System.out.println("Client " + i + " Loan: " + clientLoans[i]);
    }
}