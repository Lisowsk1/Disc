public abstract class Request {
    private int reqPos;
    private final int arrivalTime;

    public Request(int pos, int arrivalTime) {
        this.reqPos = pos;
        this.arrivalTime = arrivalTime;
    }

    public int getReqPos() {
        return reqPos;
    }

    public void setReqPos(int reqPos) {
        this.reqPos = reqPos;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }


    public boolean isPriority() {
        return false;
    }

}
