import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import static java.lang.Math.abs;


public class Disc {
    public int discSize;
    //public int totalHeadMovement;
    private int headPos;
    private int startingHeadPos;
    private boolean direction = false;// direction: left is false, right is true
    private boolean priorityMode = false;

    public Disc(int startingHeadPos, int discSize) {
        this.startingHeadPos = startingHeadPos;
        this.discSize = discSize;
        headPos = startingHeadPos;
    }


    public int distance(Request req) {
        return abs(headPos - req.getReqPos());
    }

    void resetDisc() {
        headPos = startingHeadPos;
    }

    public int FCFS(LinkedList<Request> sim, int totalHeadMovement) {

        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            totalHeadMovement += distance(r);
            headPos = r.getReqPos();
            System.out.println(headPos);
            it.remove();
        }

        return totalHeadMovement;
    }

    public int SSTF_nonPriority(LinkedList<Request> sim, int totalHeadMovement) {

        sim.sort(Comparator.comparingInt(this::distance));
        Request r = sim.getFirst();
        totalHeadMovement += distance(r);
        headPos = r.getReqPos();
        sim.removeFirst();
        System.out.println(headPos);

        return totalHeadMovement;
    }

    public int SSTF_Priority(LinkedList<Request> sim, int totalHeadMovement) {
        if (priorityMode) {
            totalHeadMovement = EDF_priority(sim, totalHeadMovement);
        }
        sim.sort(Comparator.comparingInt(this::distance));
        Request r = sim.getFirst();
        totalHeadMovement += distance(r);
        headPos = r.getReqPos();
        sim.removeFirst();
        System.out.println(headPos);

        return totalHeadMovement;
    }

    public int SCAN(LinkedList<Request> sim, int totalHeadMovement) {
        // direction: left is false, right is true
        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.getReqPos() == headPos) {
                System.out.println(headPos);
                it.remove();
            }
        }
        if (headPos == 1) { //the disc is numerated 1...N
            direction = true;
        }
        if (headPos == discSize) {
            direction = false;
        }

        if (direction) {
            headPos++;
        } else {
            headPos--;
        }

        totalHeadMovement++;
        return totalHeadMovement;
    }

    public int C_SCAN(LinkedList<Request> sim, int totalHeadMovement) {
        //this C_SCAN implementation goes left to right
        //direction: left is false, right is true

        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.getReqPos() == headPos) {
                System.out.println(headPos);
                it.remove(); //removal after finishing
            }
        }
        if (headPos == discSize) {
            headPos = 1;
            totalHeadMovement++; //assume the comeback costs only one movement
        }
        headPos++;
        totalHeadMovement++;
        return totalHeadMovement;
    }

    public int EDF_priority(LinkedList<Request> sim, int totalHeadMovement) {
        sim.sort(Comparator.comparingInt(PriorityRequest::getDeadlineRequest));

        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.isPriority()) {
                PriorityRequest pr = (PriorityRequest) r;
                totalHeadMovement += distance(pr);
                headPos = pr.getReqPos();
                it.remove();
            }
        }
        priorityMode = false;
        return totalHeadMovement;
    }


    public int getHeadPos() {
        return headPos;
    }

    public void setHeadPos(int headPos) {
        this.headPos = headPos;
    }

    public int getStartingHeadPos() {
        return startingHeadPos;
    }

    public void setStartingHeadPos(int startingHeadPos) {
        this.startingHeadPos = startingHeadPos;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public boolean isPriorityMode() {
        return priorityMode;
    }

    public void setPriorityMode(boolean priorityMode) {
        this.priorityMode = priorityMode;
    }
}
