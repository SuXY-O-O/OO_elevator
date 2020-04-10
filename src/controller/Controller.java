package controller;

import datacenter.ControllerToElevator;
import datacenter.ListenerToController;
import elevators.Elevator;
import source.Compatibility;
import source.PersonInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class Controller extends Thread {
    private ListenerToController ltc;
    private ConcurrentHashMap<String, ControllerToElevator> cte = new ConcurrentHashMap<>();
    private final ArrayList<Elevator> elevators = new ArrayList<>();
    private Compatibility[] cs  = new Compatibility[6];

    public Controller(ListenerToController ltc) {
        this.ltc = ltc;
    }

    // Build a tray between controller and a new elevator
    // initial an elevator
    public void addElevator(String id, String type) {
        ControllerToElevator cteTmp = new ControllerToElevator(id, type);
        cte.put(id, cteTmp);
        Elevator eleTmp = new Elevator(cteTmp, id, type);
        synchronized (elevators) {
            elevators.add(eleTmp);
            notifyAll();
        }
    }

    @Override
    public void run() {
        // Get the people in
        PersonInfo p = ltc.take();
        while (p != null) {
            // Start elevators
            synchronized (elevators) {
                if (!elevators.isEmpty()) {
                    for (Elevator e : elevators) {
                        e.start();
                    }
                    elevators.clear();
                }
                notifyAll();
            }
            // Get Compatibility if an elevator can serve the person
            int i = 0;
            for (ControllerToElevator tmp : cte.values()) {
                Compatibility c = tmp.checkCompatibility(p);
                if (c.isCanTake()) {
                    cs[i] = tmp.checkCompatibility(p);
                    i++;
                }
            }
            if (i == 0) {
                System.out.println("\nDo not put IN " + p + "\n");
            }
            // Check the beat fit elevator
            Arrays.sort(cs, 0, i);
            cte.get(cs[0].getId()).addPerson(p);
            // Get next person request
            p = ltc.take();
        }
        // Tell the elevators the input is end
        for (ControllerToElevator tmp : cte.values()) {
            tmp.setCanStop();
        }
    }
}
