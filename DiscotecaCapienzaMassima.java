import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiscotecaCapienzaMassima {

    private static final int CAPACITA_MAX = 161;  // Capacità massima della discoteca
    private int personeDentro = 0;  // Numero attuale di persone nella discoteca
    private final List<String> gruppiInAttesa = new ArrayList<>();  // Gruppi in attesa
    private final Object lock = new Object();  // Lock per la sincronizzazione

    // Metodo per far entrare un gruppo
    public void entraGruppo(String nomeGruppo, int nPersone) throws InterruptedException {
        synchronized (lock) {
            // Controlla se ci sono posti disponibili
            while (personeDentro + nPersone > CAPACITA_MAX) {
                // Aggiungi il gruppo in attesa
                gruppiInAttesa.add(nomeGruppo);
                System.out.println(nomeGruppo + " è in attesa di entrare.");
                lock.wait();  // Aspetta che ci sia posto disponibile
            }

            // Se c'è posto, il gruppo entra
            personeDentro += nPersone;
            gruppiInAttesa.remove(nomeGruppo);  // Rimuovi il gruppo dall'attesa
            System.out.println(nomeGruppo + " è entrato con " + nPersone + " persone. Persone dentro: " + personeDentro);
        }
    }

    // Metodo per far uscire un gruppo
    public void esceGruppo(String nomeGruppo, int nPersone) {
        synchronized (lock) {
            personeDentro -= nPersone;
            System.out.println(nomeGruppo + " è uscito con " + nPersone + " persone. Persone dentro: " + personeDentro);
            lock.notifyAll();  // Avvisa tutti i gruppi in attesa
        }
    }

    // Metodo per stampare lo stato attuale
    public void stampaStato() {
        synchronized (lock) {
            System.out.println("Persone attualmente nella discoteca: " + personeDentro);
            System.out.println("Gruppi in attesa: " + gruppiInAttesa);
        }
    }

    public static void main(String[] args) {
        DiscotecaCapienzaMassima discoteca = new DiscotecaCapienzaMassima();
        Random random = new Random();

        // Creiamo e avviamo i thread per i gruppi
        for (int i = 0; i < 10; i++) {
            String nomeGruppo = "Gruppo-" + (i + 1);
            int nPersone = random.nextInt(5) + 5;  // Ogni gruppo avrà tra 5 e 10 persone

            new Thread(() -> {
                try {
                    while (true) {
                        discoteca.entraGruppo(nomeGruppo, nPersone);
                        // Rimane all'interno per un tempo casuale (es: tra 1 e 3 secondi)
                        Thread.sleep(random.nextInt(2000) + 1000);
                        discoteca.esceGruppo(nomeGruppo, nPersone);
                        // Rimane fuori per un tempo casuale (es: tra 1 e 2 secondi)
                        Thread.sleep(random.nextInt(2000) + 1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // Stampa periodica dello stato ogni secondo
        new Thread(() -> {
            try {
                while (true) {
                    discoteca.stampaStato();
                    Thread.sleep(1000);  // Stampa ogni secondo
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
