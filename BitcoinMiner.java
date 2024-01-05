import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BitcoinMiner {
    public static void main(String[] args) throws Exception {
        int coreCount = Runtime.getRuntime().availableProcessors() - 1;
        System.out.println("Available processors: " + coreCount);

        ExecutorService threadPool = Executors.newFixedThreadPool(coreCount);
        BlockingQueue<Long> nonceQueue = new LinkedBlockingQueue<>(256 * coreCount);
        List<Future<Long>> futures = new ArrayList<>();

        createHashCheckers(coreCount, threadPool, nonceQueue, futures);
        sendNonces(nonceQueue, coreCount);

        finishUp(nonceQueue, futures, threadPool);
    }

    static void createHashCheckers(int coreCount, ExecutorService threadPool, BlockingQueue<Long> nonceQueue, List<Future<Long>> futures) {
        for (int i = 0; i < coreCount; i++) {
            futures.add(threadPool.submit(() -> createHashChecker(nonceQueue)));
        }
    }

    private static Long createHashChecker(BlockingQueue<Long> nonceQueue) {
        BlockChainData data = BlockChainData.getDataForBlock815296();
        while (true) {
            try {
                Long nonce = nonceQueue.take();
                if (nonce == Utils.SENTINEL) {
                    return Utils.NOT_FOUND_SOLUTION;
                } else if (data.tryBitcoinHash(nonce)) {
                    return nonce;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    static void sendNonces(BlockingQueue<Long> nonceQueue, int coreCount) throws Exception {
        long startTime = System.nanoTime();

        for (long nonce = Utils.NONCE_RANGE_START; nonce < Utils.NONCE_RANGE_END; nonce++) {
            nonceQueue.put(nonce);

            if ((nonce % Utils.STEPS_BETWEEN_CHECKS) == 0) {
                if (checkFoundSolution(coreCount)) {
                    break;
                }
                Utils.printHashRate(nonce, System.nanoTime() - startTime);
            }
        }
    }

    private static boolean checkFoundSolution(int coreCount) {
        return Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getName().startsWith("pool-"))
                .limit(coreCount)
                .allMatch(Thread::isInterrupted);
    }

    static void finishUp(BlockingQueue<Long> nonceQueue, List<Future<Long>> futures, ExecutorService threadPool) {
        sendSentinels(nonceQueue, futures);
        threadPool.shutdownNow();
    }

    static void sendSentinels(BlockingQueue<Long> nonceQueue, List<Future<Long>> futures) {
        nonceQueue.clear();
        for (int i = 0; i < futures.size(); i++) {
            try {
                nonceQueue.put(Utils.SENTINEL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    static void finishUp(List<Future<Long>> futures) {
    futures.forEach(future -> future.cancel(true));
    }

    static boolean checkFoundSolution(List<Future<Long>> tasks) {
        for (Future<Long> task : tasks) {
            try {
                Long solution = task.get(0, TimeUnit.MILLISECONDS);
                if (solution != null && solution != Utils.NOT_FOUND_SOLUTION) {
                    Utils.printSolution(solution);
                    return true;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // Ignoring exceptions, continue checking other tasks
            }
        }
        return false;
    }

    static long getSolution(List<Future<Long>> tasks) throws InterruptedException, ExecutionException {
        for (Future<Long> task : tasks) {
            try {
                Long solution = task.get(0, TimeUnit.MILLISECONDS);
                if (solution != null && solution != Utils.NOT_FOUND_SOLUTION) {
                    return solution;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // Ignoring exceptions, continue checking other tasks
            }
        }
        return Utils.NOT_FOUND_SOLUTION;
    }

}
