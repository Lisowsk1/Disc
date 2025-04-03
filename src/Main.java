import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

public class Main {


    //grandPlan is the original copy, for other algorithm to be tested on exactly the same data
    //plan is disposable for the sim to run efficiently by 'moving' it to sim at their arrival times
    //sim is list of requests that arrived, algorithms have access only to this list
    //request is removed after executing from sim


    public static LinkedList<Request> grandPlan = new LinkedList<>();
    public static LinkedList<Request> plan = new LinkedList<>();
    public static LinkedList<Request> sim = new LinkedList<>();
    public static int totalHeadMovement = 0;
    public static int time = 0;

    //TESTS


    // Global parameters
    public static final int NUM_REQUESTS = 5;
    public static int MAX_POSITION = 2000;  // Valid positions: 1 to 2000

    /**
     * A generic random test.
     * Generates 20,000 requests with uniformly random positions and arrival times.
     */
    public static void testRandomRequests() {
        grandPlan.clear();
        Random rand = new Random();
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int position = 1 + rand.nextInt(MAX_POSITION); // positions 1 to 2000
            int arrivalTime = 1 + rand.nextInt(10000);       // arbitrary arrival time > 0
            grandPlan.add(new NormalRequest(position, arrivalTime));
        }
        // Pass grandPlan to your algorithm test harness
    }

    /**
     * Test for FCFS weaknesses.
     * Alternates extreme positions to force large head jumps.
     */
    public static void testFCFSWeakness() {
        grandPlan.clear();
        int time = 0;
        for (int i = 0; i < NUM_REQUESTS; i++) {
            // Alternate between the two extremes
            int position = (i % 2 == 0) ? 1 : MAX_POSITION;
            time += 5; // Increment time in fixed steps to simulate head movement cost
            grandPlan.add(new NormalRequest(position, time));
        }
        // Pass grandPlan to the FCFS algorithm for testing
    }

    /**
     * Test for SSTF weaknesses.
     * Clusters most requests around a central value (e.g., 1000) while every 50th request
     * is forced to an extreme. This may highlight starvation issues.
     */
    public static void testSSTFWeakness() {
        grandPlan.clear();
        int time = 0;
        int basePosition = 1000;
        Random rand = new Random();
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int position;
            if (i % 50 == 0) {
                // Every 50th request is at one extreme or the other
                position = (i % 100 == 0) ? 1 : MAX_POSITION;
            } else {
                // Cluster near the center with a small random deviation
                position = basePosition + rand.nextInt(21) - 10; // [basePosition-10, basePosition+10]
                if (position < 1) position = 1;
                if (position > MAX_POSITION) position = MAX_POSITION;
            }
            time += 1 + rand.nextInt(5); // small time increments
            grandPlan.add(new NormalRequest(position, time));
        }
        // Pass grandPlan to the SSTF algorithm for testing
    }

    /**
     * Test for SCAN weaknesses.
     * Simulates a steadily advancing head (from low to high) and, every 10th request,
     * spawns a new request just behind the head. The arrival times are coordinated with the
     * head movement (time is measured in head movements).
     */
    public static void testSCANWeakness() {
        grandPlan.clear();
        int time = 0;
        int headPosition = 0;
        Random rand = new Random();
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int position;
            // Occasionally generate a request behind the current head position
            if (headPosition > 10 && i % 10 == 0) {
                // Ensure the new request is behind but within the valid range
                position = headPosition - (1 + rand.nextInt(Math.min(10, headPosition)));
            } else {
                position = 1 + rand.nextInt(MAX_POSITION);
            }
            time++; // each iteration represents one head movement unit
            grandPlan.add(new NormalRequest(position, time));
            // Simulate the head moving forward one step per iteration (for test coordination)
            headPosition++;
            if (headPosition > MAX_POSITION) {
                headPosition = MAX_POSITION;  // cap the head position at the upper bound
            }
        }
        // Pass grandPlan to the SCAN algorithm for testing
    }

    /**
     * Test for C-SCAN weaknesses.
     * In C-SCAN the head moves only in one direction and then jumps back to the start.
     * This test forces requests to be generated near the beginning periodically, simulating
     * the scenario where new requests miss the current pass.
     */
    public static void testCSCANWeakness() {
        grandPlan.clear();
        int time = 0;
        Random rand = new Random();
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int position;
            // Every 20th request spawns near the beginning
            if (i % 20 == 0) {
                position = 1 + rand.nextInt(50); // positions in the low range
            } else {
                position = 1 + rand.nextInt(MAX_POSITION);
            }
            time += 1 + rand.nextInt(3); // time advances slowly
            grandPlan.add(new NormalRequest(position, time));
        }
        // Pass grandPlan to the C-SCAN algorithm for testing
    }

    public static void main(String[] args) {

        Disc disc = new Disc(1, MAX_POSITION);

        //INSERT TEST
        testRandomRequests();

        //plan has to be sorted by arrivalTime!!!
        grandPlan.sort(Comparator.comparingInt(Request::getArrivalTime));

        plan.addAll(grandPlan);

        // for (int i = 0; i <n; i++) { // to be continued for testing all of them in row

        time = 0;
        while (!plan.isEmpty() || !sim.isEmpty()) {//main loop that updates functions ensuring all requests are completed by the end
            while (!plan.isEmpty()) {//the scenario loop, adds from plan to sim at their arrival times;
                Request r = plan.getFirst();

                if (r.getArrivalTime() <= time) {
                    if (r.isPriority()) {
                        disc.setPriorityMode(true);
                    }
                    sim.add(r);
                    plan.removeFirst();
                } else
                    break; //all relevant request added; ! list has to be sorted by arrival timers or this breaks too early !
            }
            totalHeadMovement = disc.FCFS(sim, totalHeadMovement);
            time++;
        }
        System.out.println("Total head movements: " + totalHeadMovement);
        System.out.println("Starved: " + disc.getStarvedRequests());
        System.out.println("Working: " + disc.getWorking());


    }
}
