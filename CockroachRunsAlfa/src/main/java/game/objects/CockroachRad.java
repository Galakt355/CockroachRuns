package game.objects;

import game.Game;

public class CockroachRad extends Cockroach{

    public CockroachRad(int startX, int startY, int id, String name) {
        positionByX = startX;
        positionByY = startY;
        CockroachId = id;
        CockroachName = name;
    }

    public int getPositionByX() {
        return positionByX;
    }
    public int getPositionByY() {
        return positionByY;
    }
    public int getId() {
        return CockroachId;
    }
    public String getName() {
        return CockroachName;
    }
    public void setPositionByX(int x) {
        positionByX = x;
    }

    @Override
    public void run() {
//        int i = 0;
        do {
            try {
                int randomDirection = 10 + (int)(Math.random() * ((500 - 10) + 1));
                move();
//                System.out.println("Рад таракан из потока " + CockroachId + " шаг: " + i++);
                Thread.sleep(randomDirection);
            } catch (InterruptedException e) {
            }
        } while (!needKill);
        if (needKill){
            Game.finishedCockroaches.add(this.CockroachName);
        }
        cockroachThreadDestroyed = true;
//        Game.killCockroachById(CockroachId);
        System.out.println("Поток " + CockroachId + " уничтожен !");
    }

}
