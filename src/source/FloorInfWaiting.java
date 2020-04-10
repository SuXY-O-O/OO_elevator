package source;

import java.util.ArrayList;

public class FloorInfWaiting {
    private ArrayList<PersonInfo> upList = new ArrayList<>();
    private ArrayList<PersonInfo> downList = new ArrayList<>();

    // Add a new person
    public void addPerson(PersonInfo p) {
        if (!p.getAction()) {
            downList.add(p);
        } else {
            upList.add(p);
        }
    }

    // If someone waiting up or not
    public boolean haveWaitingUp() {
        return !(upList.isEmpty());
    }

    // If someone waiting down or not
    public boolean haveWaitingDown() {
        return !(downList.isEmpty());
    }

    /* Search for num of person who want to go upstairs
    num = -1 : get all person
    num >= 0 : get num person
     */
    public ArrayList<PersonInfo> getInUp(int num) {
        return getPersonRequests(num, upList);
    }

    /* Search for num of person who want to go downstairs
    num = -1 : get all person
    num >= 0 : get num person
     */
    public ArrayList<PersonInfo> getInDown(int num) {
        return getPersonRequests(num, downList);
    }

    // For get list
    private ArrayList<PersonInfo> getPersonRequests(int num, ArrayList<PersonInfo> list) {
        assert (num >= -1);
        ArrayList<PersonInfo> tmp = new ArrayList<>();
        if (num == -1 || num >= list.size()) {
            tmp.addAll(list);
            list.clear();
        } else {
            tmp.addAll(list.subList(0, num));
            list.subList(0, num).clear();
        }
        return tmp;
    }
}
