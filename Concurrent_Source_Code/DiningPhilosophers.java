import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class DiningPhilosophers
{
    static enum State
    {
        THINKING, HUNGRY, EATING;
    }
    static int left(int i, int N) 
    {  
        // number of the left neighbor of philosopher i, for whom both forks are available
        return (i - 1 + N) % N; // N is added for the case when  i - 1 is negative
    }

    static int right(int i, int N) 
    {  
        // number of the right neighbour of the philosopher i, for whom both forks are available
        return (i + 1) % N;
    }    
    static void test(int i, int N, Semaphore[] both_forks_available, State[] state) 
    // if philosopher i is hungry and both neighbours are not eating then eat
    { 
        // i: philosopher number, from 0 to N-1
        if (state[i] == State.HUNGRY &&
            state[left(i, N)] != State.EATING &&
            state[right(i, N)] != State.EATING) 
        {
            state[i] = State.EATING;
            both_forks_available[i].release(); // forks are no longer needed for this eat session
        }
    }
    static void eat(int i, Object output_mtx)
    {
        int duration = ThreadLocalRandom.current().nextInt(400, 800);
        synchronized (output_mtx) {
            System.out.println("\t\t\t\t" + i + " is eating " + duration + "ms");
        }
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {}
    }
    static void put_forks(int i, int N, Object critical_region_mtx, Semaphore[] both_forks_available, State[] state) 
    { 
        synchronized (critical_region_mtx) {
            state[i] = State.THINKING;  // philosopher has finished State::EATING
            test(left(i, N), N, both_forks_available, state);               // see if left neighbor can now eat
            test(right(i, N), N, both_forks_available, state);             // see if right neighbor can now eat
                                        // exit critical region by exiting the function
        }
    }
    static void think(int i, Object output_mtx) 
    {
        int duration = ThreadLocalRandom.current().nextInt(400, 800);
        synchronized (output_mtx) {
            System.out.println(i + " is thinking " + duration + "ms");
        }
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {}
    }
    static void take_forks(int i, int N, Object output_mtx, Object critical_region_mtx, Semaphore[] both_forks_available, State[] state)
    {
        synchronized (critical_region_mtx)
        {
            state[i] = State.HUNGRY;  // record fact that philosopher i is State::HUNGRY
            synchronized (output_mtx) {
                System.out.println("\t\t" + i + " is State.HUNGRY");
            }
            test(i, N, both_forks_available, state);                        // try to acquire (a permit for) 2 forks
        }                                   // exit critical region
        try {
            both_forks_available[i].acquire();  // block if forks were not acquired
        } catch (InterruptedException e) {}
    }

    public static void main(String[] args)
    {
        System.out.println("dp_14");
        final int N = 5;
        Semaphore both_forks_available[] = new Semaphore[N];
        Object critical_region_mtx = new Object(), output_mtx = new Object();
        Thread t[] = new Thread[N];
        State state[] = new State[N];
        for (int i = 0; i < N; i++) {
            int finali = i;
            state[i] = State.THINKING;
            both_forks_available[i] = new Semaphore(0);
            t[i] = new Thread(()->{
                while (true) {
                    think(finali, output_mtx);
                    take_forks(finali, N, output_mtx, critical_region_mtx, both_forks_available, state);
                    eat(finali, output_mtx);
                    put_forks(finali, N, critical_region_mtx, both_forks_available, state);
                }
            });
        }
        for (int i = 0; i < N; i++) {
            t[i].start();
        }
        for (int i = 0; i < N; i++) {
            try {
                t[i].join();
            } catch (InterruptedException e) {}
        }
    }
}