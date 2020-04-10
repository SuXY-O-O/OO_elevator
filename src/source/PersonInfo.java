package source;

import com.oocourse.elevator3.PersonRequest;
import datacenter.ListenerToController;

// Change format of the person request
public class PersonInfo {
    private int destination;
    private int from;
    private boolean action;  // Upstairs : true; Downstairs : false
    private int id;
    private PersonInfo next;
    protected static ListenerToController ltc;

    public PersonInfo(PersonRequest p) {
        this.destination = p.getToFloor();
        this.from = p.getFromFloor();
        this.id = p.getPersonId();
        this.action = this.destination > this.from;
        this.next = null;
    }

    public PersonInfo(int from, int to, int id) {
        this.destination = to;
        this.from = from;
        this.id = id;
        this.action = this.destination > this.from;
        this.next = null;
    }

    public static void setLtc(ListenerToController ltc) {
        PersonInfo.ltc = ltc;
    }

    public void addNext(PersonInfo p) {
        this.next = p;
    }

    // Map floor number to 0 - 22
    public int getFrom() {
        return Floor.toIndex.get(from);
    }

    // Map floor number to 0 - 22
    public int getDestination() {
        return Floor.toIndex.get(destination);
    }

    // Going up or down
    public boolean getAction() {
        return action;
    }

    public int getId() {
        return id;
    }

    public void getIn(String elevatorId) {
        SafeOutput.println("IN-" + this.id + "-" + this.from + "-" + elevatorId);
    }

    public void getOff(String elevatorId) {
        SafeOutput.println("OUT-" + this.id + "-" +
                this.destination + "-" + elevatorId);
        if (next != null) {
            ltc.add(next, true);
        }
    }

    @Override
    public String toString() {
        return String.format("%d-FROM-%d-TO-%d", id, from, destination);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof PersonInfo) {
            PersonInfo tmp = (PersonInfo) obj;
            return tmp.getId() == this.id;
        } else {
            return false;
        }
    }
}
