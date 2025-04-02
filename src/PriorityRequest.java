class PriorityRequest extends Request {
    private static int deadline;

    public PriorityRequest(int pos,int arrivalTime, int deadline) {
        super(pos,arrivalTime);
        PriorityRequest.deadline = deadline;
    }

    public static int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        PriorityRequest.deadline = deadline;
    }

    @Override
    public boolean isPriority() {
        return true;
    }


    public static int getDeadlineRequest(Request request) {
        return getDeadline();
    }
}