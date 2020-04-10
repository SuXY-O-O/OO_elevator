package datacenter;

import source.PersonInfo;
import source.Compatibility;
import source.FloorInfWaiting;
import source.HeatMap;

import java.util.ArrayList;

// Tray between controller and one elevator
public class ControllerToElevator {
    private FloorInfWaiting[] floors = new FloorInfWaiting[23];
    private HeatMap heatMap;
    private boolean canStop = false;

    public ControllerToElevator(String id, String type) {
        for (int i = 0; i < 23; i++) {
            floors[i] = new FloorInfWaiting();
        }
        this.heatMap = new HeatMap(id, type);
    }

    // Set canStop to true when input is canceled
    public synchronized void setCanStop() {
        canStop = true;
        notifyAll();
    }

    // Set eaction in heatMap
    public synchronized void updateEaction(int newAction) {
        heatMap.updateEaction(newAction);
        notifyAll();
    }

    // Add a person waiting at floor num
    public synchronized void addPerson(PersonInfo p) {
        int num = p.getFrom();
        floors[num].addPerson(p);
        heatMap.addIn(p);
        notifyAll();
    }

    // Update heat map when elevator arrive at one floor
    public synchronized void updateHeatMap(int peopleNum, int floorNum) {
        heatMap.updateMap(floorNum, peopleNum);
        notifyAll();
    }

    /* Check waiting of floor num for eaction:
    1 : up,
    0 : stop,
    -1 : down
     */
    public synchronized boolean haveWaiting(int num, int eaction) {
        boolean status = false;
        try {
            if (eaction == 1) {
                status = floors[num].haveWaitingUp();
            } else if (eaction == -1) {
                status = floors[num].haveWaitingDown();
            } else {
                status = floors[num].haveWaitingUp() | floors[num].haveWaitingDown();
            }
            return status;
        } finally {
            if (!status) {
                notifyAll();
            }
        }
    }

    /* Order for person to get in
    max >= 0 : max number of person
    max = -1 : all waiting person
    num : the num where the elevator going eaction
    1 : Up
    0 : Stop
    -1 : Down
     */
    public synchronized ArrayList<PersonInfo> getInList(int max, int num, int eaction) {
        try {
            if (eaction == 1) {
                return floors[num].getInUp(max);
            } else if (eaction == -1) {
                return floors[num].getInDown(max);
            } else {
                if (floors[num].haveWaitingUp()) {
                    return floors[num].getInUp(max);
                }
                return floors[num].getInDown(max);
            }
        } finally {
            notifyAll();
        }
    }

    /* Get the floor to go when the elevator is empty
    num : the floor where the elevator at
    return int : the floor to go
    when nobody wait:
    if canStop, return -1
    else wait()
     */
    public synchronized int getNext(int num) {
        int tmp = checkWaiting(num);
        while (tmp == -1) {
            if (canStop) {
                return -1;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tmp = checkWaiting(num);
        }
        try {
            return tmp;
        } finally {
            notifyAll();
        }
    }

    // Check when someone want to get in
    public synchronized Compatibility checkCompatibility(PersonInfo p) {
        try {
            return heatMap.checkCompatibility(p);
        } finally {
            notifyAll();
        }
    }

    /* Check the waiting when the elevator is at floor num
    if found : return floor number,
    if nobody is waiting : return -1
     */
    private int checkWaiting(int num) {
        int highestDown = getHighestDown();
        int lowestUp = getLowestUp();
        if (highestDown == -1 || lowestUp >= num) {
            return lowestUp;
        } else if (lowestUp == -1 || highestDown <= num) {
            return highestDown;
        } else {
            int up = Math.abs(lowestUp - num);
            int down = Math.abs(highestDown - num);
            if (up > down) {
                return highestDown;
            }
            return lowestUp;
        }
    }

    /* Get the highest person request who want to go downstairs
    return -1 when no one goes down
     */
    private int getHighestDown() {
        for (int i = 18; i >= 0; i--) {
            if (floors[i].haveWaitingDown()) {
                return i;
            }
        }
        return -1;
    }

    /* Get the lowest person request who want to go upstairs
    return -1 when no one goes down
     */
    private int getLowestUp() {
        for (int i = 0; i < 19; i++) {
            if (floors[i].haveWaitingUp()) {
                return i;
            }
        }
        return -1;
    }
}
