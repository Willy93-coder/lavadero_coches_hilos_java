import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class App {
    public static void main(String[] args) throws Exception {
        // Creamos el limpiador de coches
        LimpiadorCoches limpiadorCoches = new LimpiadorCoches(5);
        // Array para crear los coches
        Coche[] coches = new Coche[20];
        // Creamos el semaforo
        Semaphore s = new Semaphore(limpiadorCoches.getCapacidad());
        // Bucle para crear dinamicamente los coches
        for (int i = 0; i < coches.length; i++) {
            coches[i] = new Coche(limpiadorCoches, s);
            coches[i].start();
        }
        // Esperamos a que los hilos acaben
        for (int i = 0; i < coches.length; i++) {
            coches[i].join();
        }
    }
}

class LimpiadorCoches {
    // Linkedlist para que entren y salgan coches
    private LinkedList<Coche> coches;
    // Capacidad
    private int capacidad;
    // Semaforo
    private Semaphore s1;

    public LimpiadorCoches(int capacidad) {
        this.capacidad = capacidad;
        this.s1 = new Semaphore(1); // Semaforo para que solo puedan acceder al metodo de uno en uno
        this.coches = new LinkedList<>(); // Inicializamos la linkedlist
    }

    public void limpiarCoche(Coche coche) {
        try {
            s1.acquire(); // Solo entra uno
            System.out.println(coche.getName() + " paga 5 euros");
            System.out.println(coche.getName() + " limpiandose!!");
            coches.add(coche);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

    }

    public void salir(Coche coche) {
        System.out.println(coche.getName() + " saliendo del lavadero");
        coches.remove(coche);
        s1.release(); // Sale uno
    }

    public int getCapacidad() {
        return capacidad; // Devolvemos la capacidad
    }
}

class Coche extends Thread {
    LimpiadorCoches limpiador; // Atributo limpiador
    Semaphore s; // Semaphoro que pedimos por parametro que depende de la capacidad del lavadero

    public Coche(LimpiadorCoches limpador, Semaphore s) {
        this.limpiador = limpador;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            s.acquire();
            System.out.println(getName() + " entrando al lavadero");
            limpiador.limpiarCoche(this);
            Thread.sleep(2000);
            limpiador.salir(this);
            s.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
