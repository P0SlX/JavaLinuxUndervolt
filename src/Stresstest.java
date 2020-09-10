import javax.management.relation.RoleUnresolved;
import java.util.ArrayList;

public class Stresstest {

    private final int numCore;
    private double load;
    private final ArrayList<BusyThread> listOfActivesThreads;

    public Stresstest() {
        this.numCore              = Runtime.getRuntime().availableProcessors();
        this.load                 = 1.0;
        this.listOfActivesThreads = new ArrayList<>();
    }

    public void startStress(long duration) {
        int nbCores = Runtime.getRuntime().availableProcessors();
        System.out.println("Stress test:\n    Cores: " + nbCores + " Duration: " + (duration == Long.MAX_VALUE ? "Infinite" : duration / 1000 + " sec"));

        if (this.listOfActivesThreads.isEmpty()) {
            // Create threads
            for (int i = 0; i < nbCores; i++) {
                this.listOfActivesThreads.add(new BusyThread("Thread" + i, load, duration));
                this.listOfActivesThreads.get(i).start();
            }
        } else {
            System.out.println("Stress test already running.");
        }
    }

    public void stopStress() {
        System.out.println("Stoping stress test.");
        this.listOfActivesThreads.forEach(Thread::interrupt);
        this.listOfActivesThreads.clear();
    }
}


