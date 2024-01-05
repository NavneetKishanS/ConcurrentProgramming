public class Join {
    public static void main(String[] args)
    {
        Thread[] threads = new Thread[10]; 
        for (int i = 0; i < 10; i++) {
            final int finali = i;
            threads[i] = new Thread(()->{
                for (int j = 0; j < 10; j++) {
                    System.out.println(j);
                    try {
                        threads[finali].sleep(100);
                    } catch (InterruptedException e) {}
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        try {
            for (int i = 0; i < 10; i++) threads[i].join();
        } catch (InterruptedException e) {}
    }
}