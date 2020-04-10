package source;

import java.util.ArrayList;

public class FloorInfElevator {
    // Person whose destination is this floor
    private ArrayList<ArrayList<PersonInfo>> list = new ArrayList<>(19);
    private int numIn = 0;
    private int max;
    private final String id;

    public FloorInfElevator(String id, int max) {
        for (int i = 0; i < 23; i++) {
            ArrayList<PersonInfo> tmp = new ArrayList<>();
            list.add(tmp);
        }
        this.id = id;
        this.max = max;
    }

    /* Check if someone need to get off
    return -1 : elevator is empty
    else return floor num
     */
    public int getDestination() {
        for (int i = 0; i < 19; i++) {
            if (!list.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    // Handle the people who get in
    public boolean getInElevator(ArrayList<PersonInfo> getInList) {
        if (getInList.isEmpty()) {
            return false;
        }
        for (PersonInfo p : getInList) {
            list.get(p.getDestination()).add(p);
            p.getIn(this.id);
            numIn++;
        }
        return true;
    }

    // Handle the people who get out at floor num
    public void getOffElevator(int num) {
        ArrayList<PersonInfo> tmp = list.get(num);
        if (tmp.isEmpty()) {
            return;
        }
        for (PersonInfo p : tmp) {
            p.getOff(this.id);
            numIn--;
        }
        list.get(num).clear();
    }

    // Check how many people can get in
    public int freeSpace() {
        return max - numIn;
    }

    // Get the num of people inside
    public int getNumIn() {
        return numIn;
    }

    // Check if someone will get off at floor num
    public boolean haveArrival(int num) {
        return !(list.get(num).isEmpty());
    }
}
