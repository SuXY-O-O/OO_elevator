package source;

import static source.Floor.toIndex;

public class HeatMap {
    /*
    Two map record how many
    people will go from floor m to m + 1 (upHeat)
    or m to m - 1 (downHeat);
    Two Rate record the sum of the two map;
    id : the elevator id;
    at : the floor the elevator at;
    eaction :
    0 : elevator stop
    1 : going up
    -1 : going down
     */
    private int[] upHeat = new int[22];
    private int upRate;
    private int[] downHeat = new int[22];
    private int downRate;
    private String id;
    private boolean[] stopType;
    private int at;
    private int eaction;
    private int speedRate;
    private int max;

    public HeatMap(String id, String type) {
        for (int i = 0; i < 18; i++) {
            upHeat[i] = 0;
            downHeat[i] = 0;
        }
        upRate = 0;
        downRate = 0;
        this.id = id;
        this.at = toIndex.get(1);
        eaction = 0;
        if (type.equals("A")) {
            this.stopType = ElevatorStopType.typeA;
            this.speedRate = 4;
            this.max = 6;
        } else if (type.equals("B")) {
            this.stopType = ElevatorStopType.typeB;
            this.speedRate = 5;
            this.max = 8;
        } else {
            this.stopType = ElevatorStopType.typeC;
            this.speedRate = 6;
            this.max = 7;
        }
    }

    // Update the heatMap and heatRate when someone get in
    public void addIn(PersonInfo person) {
        if (person.getAction()) {
            for (int i = person.getFrom(); i < person.getDestination(); i++) {
                upHeat[i]++;
            }
            upRate += person.getDestination() - person.getFrom();
        } else {
            for (int i = person.getFrom() - 1; i >= person.getDestination(); i--) {
                downHeat[i]++;
            }
            downRate += person.getFrom() - person.getDestination();
        }
    }

    // Update the heatMap and heatRate when the elevator arrive at one floor
    public void updateMap(int newAt, int num) {
        if (newAt > this.at) {
            upHeat[this.at] -= num;
        } else if (newAt < this.at) {
            downHeat[newAt] -= num;
        }
        this.at = newAt;
    }

    // Update the eaction when it is changed
    public void updateEaction(int newEaction) {
        this.eaction = newEaction;
    }

    // Check the Compatibility for this elevator and the person request
    public Compatibility checkCompatibility(PersonInfo person) {
        if (!stopType[person.getFrom()] || !stopType[person.getDestination()]) {
            return new Compatibility(false, false, 0, 0, 0, this.id);
        }
        boolean paction = person.getAction();
        boolean canPickUp = checkPickUp(person.getFrom(),
                person.getDestination(), paction);
        int distance = getDistance(person.getFrom(), paction);
        int rate;
        if (paction) {
            rate = upRate;
        } else {
            rate = downRate;
        }
        int max = getHeatMax(paction);
        return new Compatibility(true, canPickUp, distance, rate, max, this.id);
    }

    // For check compatibility
    // Check whether the elevator have space to pick up the person
    private boolean checkPickUp(int from, int to, boolean paction) {
        int i;
        if (this.eaction == 1) {
            if (paction && from <= this.at) {
                return false;
            }
        } else if (this.eaction == -1) {
            if (!paction && from >= this.at) {
                return false;
            }
        }
        if (paction) {
            for (i = from; i < to; i++) {
                if (upHeat[i] >= max) {
                    return false;
                }
            }
        } else {
            for (i = from - 1; i >= to; i--) {
                if (downHeat[i] >= max) {
                    return false;
                }
            }
        }
        return true;
    }

    // For check compatibility
    // Return the distance between the elevator and the person
    private int getDistance(int from, boolean paction) {
        if (eaction == 0) {
            return Math.abs(from - this.at);
        }
        int tmpAt;
        int tmpFrom;
        if (eaction == -1) {
            tmpAt = 44 - this.at;
        }
        else {
            tmpAt = this.at - 1;
        }
        if (!paction) {
            tmpFrom = 44 - from;
        }
        else {
            tmpFrom = from - 1;
        }
        int tmp = tmpFrom - tmpAt;
        if (tmp == 44 || tmp == -44) {
            tmp = 0;
        } else if (tmp < 0) {
            tmp += 44;
        }
        return tmp * speedRate;
    }

    // For check compatibility
    // Get the max heatRate of the floors the person will pass by
    private int getHeatMax(boolean action) {
        int rate = 0;
        if (action) {
            for (int i : upHeat) {
                if (i > rate) {
                    rate = i;
                }
            }
        } else {
            for (int i : downHeat) {
                if (i > rate) {
                    rate = i;
                }
            }
        }
        return rate;
    }
}