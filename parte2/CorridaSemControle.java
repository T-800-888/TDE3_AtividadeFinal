// Parte 2 – Versão com condição de corrida (sem sincronização)
import java.util.concurrent.*;

public class CorridaSemControle {
    static int count = 0;

    public static void main(String[] args) throws Exception {
        int T = 8;          // número de threads
        int M = 250_000;    // incrementos por thread

        ExecutorService pool = Executors.newFixedThreadPool(T);

        Runnable r = () -> {
            for (int i = 0; i < M; i++) {
                // Terá perda de incrementos
                count++;
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

        System.out.printf("[Sem controle] Esperado=%d, Obtido=%d, Tempo=%.3fs%n",
                esperado, count, (t1 - t0) / 1e9);
    }
}
