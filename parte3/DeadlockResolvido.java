// Parte 3 – Versão corrigida usando ordem global de aquisição
public class DeadlockResolvido {
    static final Object LOCK_A = new Object();
    static final Object LOCK_B = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> tarefaComOrdem("T1"));
        Thread t2 = new Thread(() -> tarefaComOrdem("T2"));

        t1.start();
        t2.start();
    }

    // Ambas as threads sempre adquirem LOCK_A antes de LOCK_B
    static void tarefaComOrdem(String nome) {
        synchronized (LOCK_A) {
            dormir(50);
            synchronized (LOCK_B) {
                System.out.println(nome + " concluiu");
            }
        }
    }

    static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
