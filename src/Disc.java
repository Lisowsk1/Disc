import static java.lang.Math.sqrt;

public class Disc {
    public int discWidth, discHeight;
    public boolean[][] disc = new boolean[discWidth][discHeight];
    // 0 means no request, not no data. In this simulation the data inside disc doesnt matter, only the requests and head movement.
    public double totalHeadMovement=0;
    private int posX=0,posY=0;

    public Disc(int x, int y) {
        this.discWidth = x;
        this.discHeight = y;
    }

    public double distanceFromHead(int reqPosX, int reqPosY){
        return sqrt(((reqPosX-posX)*(reqPosX-posX))+((reqPosY-posY)*(reqPosY-posY)));
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
