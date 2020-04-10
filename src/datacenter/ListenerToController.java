package datacenter;

import source.PersonInfo;
import source.Floor;

import java.util.ArrayList;

// Tray between listener and controller
public class ListenerToController {
    private static ArrayList<PersonInfo> waiting = new ArrayList<>();
    private static ArrayList<PersonInfo> turnList = new ArrayList<>();
    private boolean canStop = false;

    // Add a person request from listener
    public synchronized void add(PersonInfo p, boolean isTurn) {
        if (isTurn) {
            waiting.add(0, p);
            turnList.remove(p);
        } else if (!checkThree(p)) {
            boolean action = p.getAction();
            if (action) {
                operateUp(p);
            } else {
                operateDown(p);
            }
        }
        notifyAll();
    }

    private boolean checkThree(PersonInfo p) {
        int from = Floor.toFloor[p.getFrom()];
        int to = Floor.toFloor[p.getDestination()];
        int id = p.getId();
        PersonInfo step1;
        PersonInfo step2;
        if (from == 3) {
            if (to == 2) {
                step1 = new PersonInfo(3, 1, id);
                step2 = new PersonInfo(1, 2, id);
            } else if (to == 4) {
                step1 = new PersonInfo(3, 5, id);
                step2 = new PersonInfo(5, 4, id);
            } else {
                return false;
            }
        } else if (to == 3) {
            if (from == 2) {
                step1 = new PersonInfo(2, 1, id);
                step2 = new PersonInfo(1, 3, id);
            } else if (from == 4) {
                step1 = new PersonInfo(4, 5, id);
                step2 = new PersonInfo(5, 3, id);
            } else {
                return false;
            }
        } else {
            return false;
        }
        step1.addNext(step2);
        waiting.add(step1);
        turnList.add(step2);
        return true;
    }

    private void operateDown(PersonInfo p) {
        int from = Floor.toFloor[p.getFrom()];
        int to = Floor.toFloor[p.getDestination()];
        int id = p.getId();
        PersonInfo pre = null;
        PersonInfo tmp;
        while (from > to) {
            if (from > 15) {
                tmp = new PersonInfo(from, Math.max(to, 15), id);
                from = 15;
            } else if (from == 15) {
                if (to <= 1) {
                    tmp = new PersonInfo(15, Math.max(1, to), id);
                    from = 1;
                } else {
                    tmp = new PersonInfo(15, Math.max(5, to), id);
                    from = 5;
                }
            } else if (from > 5) {
                tmp = new PersonInfo(from, Math.max(to, 5), id);
                from = 5;
            } else if (from > 1) {
                tmp = new PersonInfo(from, Math.max(1, to), id);
                from = 1;
            } else if (from > -2) {
                tmp = new PersonInfo(from, Math.max(-2, to), id);
                from = -2;
            } else {
                tmp = new PersonInfo(-2, -3, id);
                from = -3;
            }
            if (pre != null)
            {
                pre.addNext(tmp);
                turnList.add(tmp);
            } else {
                waiting.add(tmp);
            }
            pre = tmp;
        }
    }

    private void operateUp(PersonInfo p) {
        int from = Floor.toFloor[p.getFrom()];
        int to = Floor.toFloor[p.getDestination()];
        int id = p.getId();
        PersonInfo pre = null;
        PersonInfo tmp;
        while (from < to) {
            if (from == -3) {
                tmp = new PersonInfo(-3, -2, id);
                from = -2;
            } else if (from < 1) {
                tmp = new PersonInfo(from, Math.min(to, 1), id);
                from = 1;
            } else if (from == 1) {
                if (to >= 15) {
                    tmp = new PersonInfo(1, Math.min(15, to), id);
                    from = 15;
                } else {
                    tmp = new PersonInfo(1, Math.min(5, to), id);
                    from = 5;
                }
            } else if (from < 5) {
                tmp = new PersonInfo(from, Math.min(5, to), id);
                from = 5;
            } else if (from < 15) {
                tmp = new PersonInfo(from, Math.min(15, to), id);
                from = 15;
            } else {
                tmp = new PersonInfo(from, Math.min(20, to), id);
                from = 20;
            }
            if (pre != null)
            {
                pre.addNext(tmp);
                turnList.add(tmp);
            } else {
                waiting.add(tmp);
            }
            pre = tmp;
        }
    }

    // Put a person request to controller
    public synchronized PersonInfo take() {
        while (waiting.isEmpty()) {
            if (canStop && turnList.isEmpty()) {
                return null;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            PersonInfo p = waiting.get(0);
            waiting.remove(0);
            return p;
        } finally {
            notifyAll();
        }
    }

    // Set when the input is end
    public synchronized void setNoNewRequest() {
        this.canStop = true;
        notifyAll();
    }
}
