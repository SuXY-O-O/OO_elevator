package elevators;

import static source.Floor.toIndex;
import static source.Floor.toFloor;

import com.oocourse.TimableOutput;
import datacenter.ControllerToElevator;
import source.ElevatorStopType;
import source.PersonInfo;
import source.FloorInfElevator;

import java.util.ArrayList;

public class Elevator extends Thread {
    private ControllerToElevator cte;
    private final String id;
    private boolean[] stopType;
    /*
    eaction =
    1 : UP
    0 : Stop
    -1 : Down
     */
    private int eaction = 0;
    private FloorInfElevator inside;
    private int speed;

    public Elevator(ControllerToElevator dc, String id, String type) {
        cte = dc;
        this.id = id;
        if (type.equals("A")) {
            this.stopType = ElevatorStopType.typeA;
            this.speed = 400;
            this.inside = new FloorInfElevator(id, 6);
        } else if (type.equals("B")) {
            this.stopType = ElevatorStopType.typeB;
            this.speed = 500;
            this.inside = new FloorInfElevator(id, 8);
        } else {
            this.stopType = ElevatorStopType.typeC;
            this.speed = 600;
            this.inside = new FloorInfElevator(id, 7);
        }
    }

    /* Get in elevator at floor num
    when the elevator is aiming to go to floor aim
     */
    private void getIn(int num, int aim) {
        int freeSpace = inside.freeSpace();
        ArrayList<PersonInfo> list = cte.getInList(freeSpace, num, eaction);
        boolean hadGetIn = inside.getInElevator(list);
        // If elevator is empty, check the other direction \ set elevator as stop
        if (!hadGetIn && (num == aim) && (inside.getDestination() == -1)) {
            eaction = 0;
            list = cte.getInList(freeSpace, num, eaction);
            hadGetIn = inside.getInElevator(list);
            if (!hadGetIn) {
                cte.updateEaction(0);
            }
        }
    }

    // Arrive at floor num when aiming to get to floor aim
    private void arriveAt(int num, int aim) throws InterruptedException {
        TimableOutput.println("ARRIVE-" + toFloor[num] + "-" + this.id);
        cte.updateHeatMap(inside.getNumIn(), num);
        if (!stopType[num]) {
            return;
        }
        if (inside.haveArrival(num) || cte.haveWaiting(num, eaction)) {
            if (inside.freeSpace() > 0) {
                openDoor(num, aim);
            }
        }
    }

    // Open Door at floor num when aiming to get to floor aim
    private void openDoor(int num, int aim) throws InterruptedException {
        TimableOutput.println("OPEN-" + toFloor[num] + "-" + this.id);
        inside.getOffElevator(num);
        sleep(400);
        getIn(num, aim);
        TimableOutput.println("CLOSE-" + toFloor[num] + "-" + this.id);
    }

    /* Get a floor the elevator will go to
    num : floor the elevator at
    return -1 when there is no order
     */
    private int getDestination(int num) {
        /*
        When nobody is in the elevator,
        search dataCenter
        When someone is in the elevator
        transport first
         */
        int tmp;
        if ((tmp = inside.getDestination()) != -1) {
            return tmp;
        }
        return cte.getNext(num);
    }

    // Check the elevator is going up or down
    private int updateAction(int at, int to) {
        int i = 0;
        if (at > to) {
            i = -1;
        } else if (at < to) {
            i = 1;
        }
        cte.updateEaction(i);
        return i;
    }

    // Move elevator from (from) to (to)
    private void moveTo(int from, int to) throws InterruptedException {
        eaction = updateAction(from, to);
        int i = from;
        if (eaction == 1) {
            for (i++; i <= to; i++) {
                sleep(speed);
                arriveAt(i, to);
            }
        } else if (eaction == -1) {
            for (i--; i >= to; i--) {
                sleep(speed);
                arriveAt(i, to);
            }
        }
    }

    @Override
    public void run() {
        // Initial at floor 1 and stop
        int num = toIndex.get(1);
        eaction = 0;
        // Get next stop
        int nextFloor = this.getDestination(num);
        while (nextFloor != -1) {
            // If the elevator is at the nextFloor
            if (num == nextFloor) {
                try {
                    openDoor(num, nextFloor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // else the elevator need to move
            else {
                try {
                    moveTo(num, nextFloor);
                    num = nextFloor;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Get next stop
            nextFloor = this.getDestination(num);
        }
    }
}
