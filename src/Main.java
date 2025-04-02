import java.util.LinkedList;
import java.util.Random;

public class Main {


    public static LinkedList<Request> grandPlan = new LinkedList<>();
    public static LinkedList<Request> plan = new LinkedList<>();
    public static LinkedList<Request> sim = new LinkedList<>();
    public static int totalHeadMovement=0;

    static int n=4;
    public static int[] totalHeadMovementsAlgorithms  = new int[n];

    //test

    public static void randomPlan(float seed) {
        Random rand = new Random();
    }


    public static void main(String[] args) {

        grandPlan.add(new Request(98, 0));
        grandPlan.add(new Request(183, 0));
        grandPlan.add(new Request(37, 0));
        grandPlan.add(new Request(122, 0));
        grandPlan.add(new Request(14, 0));
        grandPlan.add(new Request(124, 0));
        grandPlan.add(new Request(65, 0));
        grandPlan.add(new Request(67, 0));


        Disc disc = new Disc(53, 200);

        //the grandPlan is the original copy, for other algorithm to be tested on exactly the same data
        //plan is disposable for the sim to run efficiently
        plan.addAll(grandPlan);

        //plan has to be sorted by arrivalTime!!!

       // for (int i = 0; i <n; i++) { // to be continued for testing all of them in row

            while (!plan.isEmpty() || !sim.isEmpty()) {//main loop that updates functions

                while (!plan.isEmpty()) {//the scenario loop, adds from plan to sim at their arrival times; disc movement is 1u of time
                    Request r = plan.getFirst();
                    if (r.getArrivalTime() <= totalHeadMovement) {
                        sim.add(r);
                        plan.removeFirst(); //removes from plan for performance, there is no need to waste iterator on the requests that were processed

                    } else break;
                }

                totalHeadMovement = disc.C_SCAN(sim, totalHeadMovement);
            }
            System.out.println("total head: " + totalHeadMovement);

    }
}