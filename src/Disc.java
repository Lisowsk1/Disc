import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import static java.lang.Math.abs;


//Basic info:
//All algorithms have priority handling using EDF, except FD_SCAN that has its own built in
//Priority: when priority request is detected at arrival, it activates 'priorityMode' and the algorithm  checks if this mode is activated,
// if it is, then chosen priority request handling strategy is executed until it executes all priority requests, then rest consisting only of normal requests
// are executed as normal by the chosen algorithm/
//Starving is checked after moving head, the threshold is big enough that there is no need no modify algorithms to get exact head placement when request got starved,
// also i assume the head doesnt know that process got starved if it doesnt look at it, so there is no check before moving head
//The correction in scan-type algorithms comes from the fact that it increments at first iteration, but it should count moves, not sectors it has been to

public class Disc {
    public int discSize;
    private int headPos;
    private int startingHeadPos;
    private boolean direction = true;// direction: left is false, right is true
    private boolean priorityMode = false;
    private boolean firstMovementCorrection = true;
    private int c_scanJumpCount = 0;
    private int starvingThreshold = 18000;
    private int starvedRequests = 0;


    public Disc(int startingHeadPos, int discSize) throws IndexOutOfBoundsException {
        if (startingHeadPos < 1 || startingHeadPos > discSize)
            throw new IndexOutOfBoundsException("Starting headpos must be within disc size");
        this.startingHeadPos = startingHeadPos;
        this.discSize = discSize;
        headPos = startingHeadPos;
    }

    public int distance(Request req) {
        return abs(headPos - req.getReqPos());
    }

    void resetDisc() {
        headPos = startingHeadPos;
        direction = false;
        Main.totalHeadMovement = 0;
        priorityMode = false;
        c_scanJumpCount = 0;
        starvedRequests = 0;
    }


    //ALGORITHMS:

    //FIFO order
    public int FCFS(LinkedList<Request> sim, int totalHeadMovement) {
        //priority detection
        if (priorityMode) {
            totalHeadMovement = EDF(sim, totalHeadMovement);
        }

        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            totalHeadMovement += distance(r);
            headPos = r.getReqPos();
            //System.out.println(headPos);
            System.out.println(Main.time+"t");
            System.out.println(r.getArrivalTime()+"r");
            if (Main.time - r.getArrivalTime() > starvingThreshold)
                starvedRequests++;

            it.remove();
        }
        return totalHeadMovement;
    }


    //shortest distance first
    public int SSTF(LinkedList<Request> sim, int totalHeadMovement) {
        //priority detection
        if (priorityMode) {
            totalHeadMovement = EDF(sim, totalHeadMovement);
        }
        sim.sort(Comparator.comparingInt(this::distance));
        if (!sim.isEmpty()) {
            Request r = sim.getFirst();
            totalHeadMovement += distance(r);
            headPos = r.getReqPos();

            if (Main.time - r.getArrivalTime() > starvingThreshold)
                starvedRequests++;

            sim.removeFirst();
        }
        return totalHeadMovement;
    }


    //updates after every move of head (only 1 unit at a time in current direction), executes all processes on current sector, then moves and updates
    //head move pattern: right,left,right...
    public int SCAN(LinkedList<Request> sim, int totalHeadMovement) {
        //priority detection
        if (priorityMode) {
            totalHeadMovement = EDF(sim, totalHeadMovement);
        }
        // direction: left is false, right is true
        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.getReqPos() == headPos) {
                System.out.println(headPos);

                if (Main.time - r.getArrivalTime() > starvingThreshold)
                    starvedRequests++;

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

        if (!firstMovementCorrection)//this correction is needed only once
            totalHeadMovement++;
        else
            firstMovementCorrection = false;

        return totalHeadMovement;
    }


    //updates after every move of head (only 1 unit at a time in current direction), executes all processes on current sector, then moves and updates
    //head move pattern:right, back to left edge skipping requests on the way (costs only 1 movement)
    public int C_SCAN(LinkedList<Request> sim, int totalHeadMovement) {
        //priority detection
        if (priorityMode) {
            totalHeadMovement = EDF(sim, totalHeadMovement);
        }

        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.getReqPos() == headPos) {

                if (Main.time - r.getArrivalTime() > starvingThreshold)
                    starvedRequests++;

                it.remove(); //removal after finishing
            }
        }
        if (headPos == discSize) {
            c_scanJumpCount++;
            headPos = 1;
            totalHeadMovement++; //assume the comeback costs only one movement
        }
        headPos++;

        if (!firstMovementCorrection)//this correction is needed only once
            totalHeadMovement++;
        else
            firstMovementCorrection = false;

        return totalHeadMovement;
    }


    //PRIORITY REQUEST HANDLING STRATEGIES:

    public int FD_SCAN(LinkedList<Request> sim, int totalHeadMovement) {
        sim.sort(Comparator.comparingInt(PriorityRequest::getDeadlineRequest));//sorting by smallest deadline greedly

        if (priorityMode) { //if no priority request were found, then just skip this part
            Iterator<Request> it1 = sim.iterator();//this iterator works only for this part of priority handling
            while (it1.hasNext()) {//priority handling
                Request r = it1.next();
                if (r.isPriority()) {
                    PriorityRequest pr = (PriorityRequest) r;
                    if (distance(pr) < PriorityRequest.getDeadline()) { //possible to execute, feasible
                        totalHeadMovement += distance(pr);
                        headPos = pr.getReqPos();

                        if (Main.time - r.getArrivalTime() > starvingThreshold)
                            starvedRequests++;

                        it1.remove();
                    } else { //impossible to execute, not feasible
                        starvedRequests++;
                        it1.remove();//rejected
                    }
                }
            }
            priorityMode = false;
        }

        //now the priority requests were handled only the normal should be in the sim
        Iterator<Request> it2 = sim.iterator();
        while (it2.hasNext()) {
            Request r = it2.next();

            if (r.getReqPos() == headPos) {
                System.out.println(headPos);

                if (Main.time - r.getArrivalTime() > starvingThreshold)
                    starvedRequests++;

                it2.remove();
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

        if (!firstMovementCorrection)//this correction is needed only once
            totalHeadMovement++;
        else
            firstMovementCorrection = false;

        return totalHeadMovement;
    }

    public int EDF(LinkedList<Request> sim, int totalHeadMovement) {//has to be added to other algorithm except FD_SCAN as it already detects priority
        sim.sort(Comparator.comparingInt(PriorityRequest::getDeadlineRequest));//sorting by smallest deadline greedly
        Iterator<Request> it = sim.iterator();
        while (it.hasNext()) {
            Request r = it.next();
            if (r.isPriority()) {
                PriorityRequest pr = (PriorityRequest) r;
                totalHeadMovement += distance(pr);
                headPos = pr.getReqPos();

                if (Main.time - r.getArrivalTime() > starvingThreshold)
                    starvedRequests++;

                it.remove();
            }
        }

        priorityMode = false;
        return totalHeadMovement;
    }


    public int getDiscSize() {
        return discSize;
    }

    public void setDiscSize(int discSize) {
        this.discSize = discSize;
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

    public boolean isFirstMovementCorrection() {
        return firstMovementCorrection;
    }

    public void setFirstMovementCorrection(boolean firstMovementCorrection) {
        this.firstMovementCorrection = firstMovementCorrection;
    }

    public int getC_scanJumpCount() {
        return c_scanJumpCount;
    }

    public void setC_scanJumpCount(int c_scanJumpCount) {
        this.c_scanJumpCount = c_scanJumpCount;
    }

    public int getStarvingThreshold() {
        return starvingThreshold;
    }

    public void setStarvingThreshold(int starvingThreshold) {
        this.starvingThreshold = starvingThreshold;
    }

    public int getStarvedRequests() {
        return starvedRequests;
    }

    public void setStarvedRequests(int starvedRequests) {
        this.starvedRequests = starvedRequests;
    }

    public int getWorking() {
        return Working;
    }

    public void setWorking(int working) {
        this.Working = working;
    }
}