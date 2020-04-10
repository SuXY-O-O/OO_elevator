package source;

/*
Use to check the compatibility of
a person request and an elevator
 */
public class Compatibility implements Comparable<Compatibility> {
    private final boolean canTake;
    private final boolean canPickUp;
    private final int distance;
    private final int heatRate;
    private final int heatMax;
    private final String id;

    /*
    canPickUp : whether the elevator have space to pick up
    distance : the distance between the people and the elevator
    heatRate : the sum of a heatMap
    heatMax : the max num in a heatMap
    id : the id of the checked elevator
     */
    public Compatibility(boolean canTake, boolean canPickUp,
                         int distance, int heatRate, int heatMax, String id) {
        this.canTake = canTake;
        this.canPickUp = canPickUp;
        this.distance = distance;
        this.heatRate = heatRate;
        this.heatMax = heatMax;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getHeatRate() {
        return heatRate;
    }

    public int getHeatMax() {
        return heatMax;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isCanPickUp() {
        return canPickUp;
    }

    public boolean isCanTake() {
        return canTake;
    }

    // Ues to get the beat fit elevator for a person request
    @Override
    public int compareTo(Compatibility c1) {
        if (c1.isCanPickUp()) {
            if (this.canPickUp) {
                return this.distance - c1.getDistance();
            }
            return 1;
        } else if (this.isCanPickUp()) {
            return -1;
        } else if (Math.abs(this.heatMax - c1.getHeatMax()) < 7) {
            return this.heatRate - c1.getHeatRate();
        } else {
            return this.heatMax - c1.getHeatMax();
        }
    }
}
