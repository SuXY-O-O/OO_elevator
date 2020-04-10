package controller;

import datacenter.ControllerToElevator;
import datacenter.ListenerToController;

import elevators.Elevator;
import source.Compatibility;
import source.PersonInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Controller extends Thread {
    private ListenerToController ltc;
    private HashMap<String, ControllerToElevator> cte = new HashMap<>();
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private Compatibility[] cs  = new Compatibility[6];

    /*
    Build elevatorNum of trays between controller and elevators
    Initial elevatorNum of Compatibility for check the compatibility of
    a person request and an elevator
     */
    public Controller(ListenerToController ltc) {
        this.ltc = ltc;
    }

    public void addElevator(String id, String type) {
        ControllerToElevator cteTmp = new ControllerToElevator(id, type);
        cte.put(id, cteTmp);
        Elevator eleTmp = new Elevator(cteTmp, id, type);
        elevators.add(eleTmp);
    }

    @Override
    public void run() {
        // Start elevators
        if (!elevators.isEmpty()) {
            for (Elevator e : elevators) {
                e.start();
            }
            elevators.clear();
        }
        // Get the people in
        PersonInfo p = ltc.take();
        while (p != null) {
            int i = 0;
            for (ControllerToElevator tmp : cte.values()) {
                Compatibility c = tmp.checkCompatibility(p);
                if (c.isCanTake()) {
                    cs[i] = tmp.checkCompatibility(p);
                    i++;
                }
            }
            // Check the beat fit elevator
            Arrays.sort(cs, 0, i);
            cte.get(cs[0].getId()).addPerson(p);
            p = ltc.take();
        }
        // Tell the elevators the input is end
        for (ControllerToElevator tmp : cte.values()) {
            tmp.setCanStop();
        }
    }
}
