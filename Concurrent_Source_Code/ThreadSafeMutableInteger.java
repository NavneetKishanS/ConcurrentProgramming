import java.util.stream.IntStream;
import java.util.function.IntUnaryOperator;

public class ThreadSafeMutableInteger
{
    private int safeInt;
    public int get() { return safeInt; }
    public void set(int safeInt) { this.safeInt = safeInt; }
    public ThreadSafeMutableInteger() {}
    public ThreadSafeMutableInteger(int safeInt) { set(safeInt); }
    public int getAndIncrement()
    {
        synchronized (this) {
            return safeInt++;
        }
    }
    public int getAndDecrement()
    {
        synchronized (this) {
            return safeInt--;
        }
    }
    public int getAndAdd(int v)
    {
        int oldValue;
        synchronized (this) {
            oldValue = safeInt;
            safeInt += v;
        }
        return oldValue;
    }
    public int incrementAndGet()
    {
        synchronized (this) {
            return ++safeInt;
        }
    }    
    public int decrementAndGet()
    {
        synchronized (this) {
            return --safeInt;
        }
    }
    public int addAndGet(int v)
    {
        synchronized (this) {
            return safeInt+=v;
        }
    }
    public int getAndUpdate(IntUnaryOperator iuo)
    {
        int oldValue;
        synchronized (this) {
            oldValue = safeInt;
            safeInt = iuo.applyAsInt(safeInt);
        }
        return oldValue;
    }
    public int updateAndGet(IntUnaryOperator iuo)
    {
        synchronized (this) {
            safeInt = iuo.applyAsInt(safeInt);
            return safeInt;
        }
    }
    public static void main(String[] args) {
        Thread threads[] = new Thread[10];
        ThreadSafeMutableInteger tsmi = new ThreadSafeMutableInteger();
        for (int i = 0; i < 10; i++) {
            int finali = i;
            threads[i] = new Thread(()-> {
                //IntStream.range(0, 10_000_000).forEach(_x -> { tsmi.set(tsmi.get()+1); });
                IntStream.range(0, 10_000_000).forEach(_x -> {
                    if (finali < 5) tsmi.incrementAndGet();
                    else tsmi.updateAndGet(x -> x-=2); //tsmi.decrementAndGet();
                });
            });
        }
        IntStream.range(0, 10).forEach(i -> { threads[i].start(); });
        IntStream.range(0, 10).forEach(i -> {
            try {
                threads[i].join();
            } catch (InterruptedException e) {}
        });
        System.out.println(tsmi.get()); //expected output: 100_000_000
    }
}