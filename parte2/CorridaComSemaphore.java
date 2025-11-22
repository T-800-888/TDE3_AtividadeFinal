// Parte 2 – Versão correta com Semáforo binário justo
import java.util.concurrent.*;

public class CorridaComSemaphore {
    static int count = 0;
    // Semáforo binário justo (fila FIFO)
    static final Semaphore sem = new Semaphore(1, true);

    public static void main(String[] args) throws Exception {
        int T = 8;          // número de threads
        int M = 250_000;    // incrementos por thread

        ExecutorService pool = Executors.newFixedThreadPool(T);

        Runnable r = () -> {
            for (int i = 0; i < M; i++) {
                try {
                    sem.acquire();  // entra na seção crítica
                    count++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    sem.release();  // libera a seção crítica
                }
            }
        };

        long t0 = System.nanoTime();

        for (int i = 0; i < T; i++) {
            pool.submit(r);
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);

        long t1 = System.nanoTime();
        long esperado = (long) T * M;

        System.out.printf("[Com semáforo] Esperado=%d, Obtido=%d, Tempo=%.3fs%n",
                esperado, count, (t1 - t0) / 1e9);
    }
}
