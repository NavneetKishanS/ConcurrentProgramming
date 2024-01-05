import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class CreateThreads
{
    public static class HelloThread extends Thread
    {
        private PrintWriter pw;
        private String str;
        public HelloThread(ThreadGroup tg, PrintWriter pw, String str) { super(tg, ""); this.pw = pw; this.str = str; }
        public void run()
        {
            System.out.println("Thread: " + getName());
            for (int i = 0; i < 10000; i++) {
                //System.out.print("H");System.out.print("e");System.out.print("l");
                //System.out.print("l");System.out.print("o");System.out.print("\n");
                pw.println(str);
            }
        }
    }
    public static class WorldThread implements Runnable //extends Thread
    {
        private PrintWriter pw;
        public WorldThread(PrintWriter pw) { this.pw = pw; }
        public void run()
        {
            System.out.println("Thread: " + Thread.currentThread().getName());
            for (int i = 0; i < 10000; i++) {
                //System.out.print("W");System.out.print("o");System.out.print("r");
                //System.out.print("l");System.out.print("d");System.out.print("\n");
                pw.println("World");
            }
        }
    }
    public static void main(String[] args)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(new File("out.txt")))) {
            ThreadGroup tg = new ThreadGroup("MainThreadGroup");
            Thread t1 = new HelloThread(tg, pw, "Hello");
            //Thread t2 = new HelloThread(pw, "World");
            //Thread t2 = new Thread(()->{ IntStream.range(0, 10000).forEach(i->{pw.println("World");}); });
            //Thread t2 = new Thread() { public void run() { IntStream.range(0, 10000).forEach(i->{pw.println("World");}); } };
            Thread t2 = new Thread(tg, new WorldThread(pw));
            /*Thread t2 = new Thread(new Runnable() {
                public void run() {
                    IntStream.range(0, 10000).forEach(i->{pw.println("World");});
                }
            });*/

            t1.setName("Thread1"); t2.setName("Thread2");
            t1.start(); t2.start();
            //t1.run(); t2.run();
            for (int i = 0; i < 100; i++) {
                System.out.println("Active Count: " + tg.activeCount());
                tg.list();
            }
            tg.destroy();
            try {
                t1.join(); t2.join();
            } catch (InterruptedException e) {}
        } catch (IOException e) {}
    }
}