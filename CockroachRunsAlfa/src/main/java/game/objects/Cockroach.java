package game.objects;
import game.Game;

public abstract class Cockroach implements Runnable{

    public volatile boolean needKill;
    public volatile boolean cockroachThreadDestroyed;

    protected volatile int CockroachId;
    protected volatile String CockroachName;
    protected volatile int positionByX;
    protected volatile int positionByY;

    public synchronized void move(){
        positionByX++;
        if (positionByX >= Game.FieldWidthWithoutIndent){
            this.needKill = true;
        }
    }
}
