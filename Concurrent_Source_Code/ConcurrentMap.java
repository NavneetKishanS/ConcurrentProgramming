import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
//import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class ConcurrentMap
{
    public static void main(String[] args) {
        //Map<Long, String> meetings = Collections.synchronizedMap(new HashMap<Long, String>());
        java.util.concurrent.ConcurrentMap<Long, String> meetings = new ConcurrentHashMap<Long, String>();
        ExecutorService es = Executors.newFixedThreadPool(10+10+1);
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            int finali = i;
            es.submit(() -> {
                for (int j = 0; j < 5000; j++) {
                    Long r = ThreadLocalRandom.current().nextLong(startTime, startTime+5000*10+1);
                    Long check = r;
                    synchronized (meetings) {
                        for (check = r; check < r + 10; check++) {
                            if (meetings.containsKey(check)) break;
                        }
                        if (check == r + 10)
                            meetings.put(r, "Meeting " + (finali*5000+j));
                        else j--;
                    }
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            es.submit(() -> {
                for (int j = 0; j < 2500; j++) {
                    synchronized (meetings) {
                        for (Long k : meetings.keySet()) {
                            meetings.remove(k);
                            break;
                        }
                    }
                }
            });
        }
        Future<?> f = es.submit(() -> {
            while (true) {
                Long currentTime = System.currentTimeMillis();
                if (currentTime > startTime + 50*1000) break;
                for (Long k = currentTime; k < currentTime + 50*1000; k++) {
                    if (meetings.containsKey(k)) {
                        System.out.println("Next Meeting: " + meetings.get(k) + " @ " + k);
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
            }
        });
        try {
            f.get();
        } catch (InterruptedException|ExecutionException e) {}
        es.shutdownNow();
        try {
            es.awaitTermination(5000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {}
    }
    //HashMap fastest, then ConcurrentHashMap, then synchronizedMap but synchronizedMap is faster than synchronized+ConcurrentHashMap
}