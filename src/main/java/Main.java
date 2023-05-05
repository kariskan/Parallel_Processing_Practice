import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    private static final int TASK_COUNT = 20; // 총 작업 개수
    private static final int THREAD_COUNT = 5; // 스레드 풀 크기
    private static final int MAX_WORK_TIME = 5000; // 최대 작업 소요 시간

    // create thread pool
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    // create Fork Join Pool
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(THREAD_COUNT);

    public static void main(String[] args) {

        int[] numArray = new int[TASK_COUNT];

        for (int i = 0; i < TASK_COUNT; i++) {
            numArray[i] = i + 1;
        }

        threadPoolTest(numArray);
        forkJoinPoolTest(numArray);
    }

    private static void threadPoolTest(int[] numArray) {
        System.out.println("------------START------------");

        // 스레드 풀을 이용한 작업 처리
        for (int i = 0; i < TASK_COUNT; i++) {
            final int idx = i;
            executor.execute(() -> {
                System.out.println("Task " + (idx + 1) + " started by thread " + Thread.currentThread().getName());
                long res = sumArray(numArray, 0, idx);
                System.out.println("Task " + (idx + 1) + " completed by thread " + Thread.currentThread().getName()
                        + " with result " + res);
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("------------FINISH------------");
    }

    private static long sumArray(int[] numArray, int start, int end) {
        int sum = 0;
        for (int i = start; i < end; i++) {
            try {
                // 작업 소요 시간 랜덤 설정
                Thread.sleep((long) (Math.random() * MAX_WORK_TIME));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sum += numArray[i];
        }
        return sum;
    }

    private static void forkJoinPoolTest(int[] numArray) {
        System.out.println("------------START------------");

        long res = FORK_JOIN_POOL.invoke(new SumTask(numArray, 0, numArray.length));
        System.out.println("res = " + res);

        FORK_JOIN_POOL.shutdown();
        System.out.println("------------FINISH------------");

    }

    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 3;
        private final int[] numArray;
        private final int start;
        private final int end;

        public SumTask(int[] numArray, int start, int end) {
            this.numArray = numArray;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {

            if (end - start <= THRESHOLD) {
                System.out.println(
                        "Current Task doing self by thread " + Thread.currentThread().getName() + ", start: " + start
                                + ", end: " + end);
                return sumArray(numArray, start, end);
            }

            System.out.println(
                    "Current Task will be splitted by thread " + Thread.currentThread().getName() + ", start: " + start
                            + ", end: " + end);

            int mid = (start + end) / 2;

            SumTask leftTask = new SumTask(numArray, start, mid);
            SumTask rightTask = new SumTask(numArray, mid, end);

            leftTask.fork();
            rightTask.fork();

            long rightResult = rightTask.join();
            long leftResult = leftTask.join();

            System.out.println("leftResult: " + leftResult + ", rightResult: " + rightResult + ", current thread: "
                    + Thread.currentThread().getName() + ", start: " + start + ", end: " + end);

            return leftResult + rightResult;
        }
    }
}