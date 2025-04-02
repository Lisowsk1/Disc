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

        /*
        grandPlan.add(new NormalRequest(98, 0));
        grandPlan.add(new NormalRequest(183, 0));
        grandPlan.add(new NormalRequest(37, 0));
        grandPlan.add(new NormalRequest(122, 0));
        grandPlan.add(new PriorityRequest(132,10,50));
        grandPlan.add(new NormalRequest(14, 10));
        grandPlan.add(new NormalRequest(124, 10));
        grandPlan.add(new NormalRequest(65, 10));
        grandPlan.add(new NormalRequest(67, 10));


         */
        grandPlan.add(new NormalRequest(10,0));
        grandPlan.add(new NormalRequest(20,5));
        grandPlan.add(new PriorityRequest(50,5,50));



        Disc disc = new Disc(0, 200);

        //the grandPlan is the original copy, for other algorithm to be tested on exactly the same data
        //plan is disposable for the sim to run efficiently
        plan.addAll(grandPlan);

        //plan has to be sorted by arrivalTime!!!

       // for (int i = 0; i <n; i++) { // to be continued for testing all of them in row

            while (!plan.isEmpty() || !sim.isEmpty()) {//main loop that updates functions

                while (!plan.isEmpty()) {//the scenario loop, adds from plan to sim at their arrival times; disc movement is 1u of time
                    Request r = plan.getFirst();
                    System.out.println(r.getReqPos());
                    if (r.getArrivalTime() <= totalHeadMovement) {
                        if (r.isPriority()) {
                            disc.setPriorityMode(true);
                        }
                        sim.add(r);
                        plan.removeFirst(); //removes from plan for performance, there is no need to waste iterator on the requests that were processed
                    }
                    else break;
                }
                totalHeadMovement = disc.SSTF_Priority(sim, totalHeadMovement);
            }
            System.out.println("total head: " + totalHeadMovement);

    }
}