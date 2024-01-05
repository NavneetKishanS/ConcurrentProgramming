import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadersWriters {
    private static final int NUMBER_OF_PROCESSES = 100;
    private static final int DATABASE_SIZE = 100;
    private static final int WRITER_RATIO = 5;
    private static final int MAX_VALUE = 1000;
    private static final int DELAY = 100;

    private static int[] dataBase = new int[DATABASE_SIZE];

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        ReadWriteLock rwl = new ReentrantReadWriteLock(true);
        Lock rl = rwl.readLock();
        Lock wl = rwl.writeLock();
        for (int i = 0; i < NUMBER_OF_PROCESSES; ++i) {
            pool.submit(() -> {
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                int index = rnd.nextInt(DATABASE_SIZE);

                if (rnd.nextInt(WRITER_RATIO) == 0) { //20%
                    int value = rnd.nextInt(MAX_VALUE);
                    // TODO: Prevent race conditions.
                    sleep(DELAY);
                    wl.lock();
                    dataBase[index] = value;
                    wl.unlock();
                    System.err.println("Written value " + value + " to index " + index);
                } else {
                    int value;
                    // TODO: Prevent race conditions.
                    sleep(DELAY);
                    rl.lock();
                    value = dataBase[index];
                    rl.unlock();
                    System.err.println("Read value " + value + " from index " + index);
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
    }

    static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}