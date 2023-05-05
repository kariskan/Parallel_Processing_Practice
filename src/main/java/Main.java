import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int TASK_COUNT = 20; // 총 작업 개수
    private static final int THREAD_COUNT = 5; // 스레드 풀 크기
    private static final int MAX_WORK_TIME = 5000; // 최대 작업 소요 시간

    // create thread pool
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {

        int[] numArray = new int[TASK_COUNT];

        for (int i = 0; i < TASK_COUNT; i++) {
            numArray[i] = i + 1;
        }

        System.out.println("------------START------------");

        // 스레드 풀을 이용한 작업 처리
        for (int i = 0; i < TASK_COUNT; i++) {
            final int idx = i;
            executor.execute(() -> {
                System.out.println("Task " + (idx + 1) + " started by thread " + Thread.currentThread().getName());
                int res = sumArray(numArray, idx);
                System.out.println("Task " + (idx + 1) + " completed by thread " + Thread.currentThread().getName()
                        + " with result " + res);
            });
        }

        executor.shutdown();
        while(!executor.isTerminated()){
        }
        System.out.println("------------FINISH------------");

    }

    private static int sumArray(int[] numArray, int end) {
        int sum = 0;
        for (int i = 0; i < end; i++) {
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
}