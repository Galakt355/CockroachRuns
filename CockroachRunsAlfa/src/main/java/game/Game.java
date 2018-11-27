package game;

import game.objects.CockroachRad;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Game extends JPanel{

    public static int CockroachNumber = 11;

    private Timer timer;
    private boolean singleClick = true;

    public static volatile boolean killAll ;
    public static volatile boolean gameOver;
    public static volatile boolean gameBegin;
    public static volatile int maxCoordinateX;
    public static volatile int leadingCockroachID;

    public static CockroachRad hurriedCockroach;
    public static volatile String leadingCockroachName = "";
    public static volatile boolean GameRestart;
    public static volatile boolean GamePause;
    public static boolean IsStartNewGame;

    public static final int FieldWidth = 680;
    public static final int Indent = 300;
    public static final int FieldWidthWithoutIndent = FieldWidth - Indent;
    public static final int FieldHeight = CockroachNumber * 20;
    public static final int CockroachThickness = 20;
    public static final int CockroachLength = 50;

    private String message;

    public volatile static ArrayList<CockroachRad> listCockroach = new ArrayList<>();
    public volatile static ArrayList<String> listCockroachName = new ArrayList<>();
    public volatile static ArrayList<String> finishedCockroaches = new ArrayList<>();

    public Game(){
        threadGame();
        addDoubleClickMouseListener();
    }

    public static void main(String[] args) {
//        Получение параметра передаваемого приложению
//        try {
//            CockroachNumber = Integer.parseInt(args[0]);
//        }catch (Exception ex){
//            System.out.println("Параметр должен быть цифрой");
//        }

        startInterface();
        GamePause = true;
    }

    public static void startInterface() {
        final JFrame startFrame = new JFrame("Меню игры");
        startFrame.setLayout(null);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setSize(280, 220);
        startFrame.setVisible(true);

        final JButton newGame = new JButton("Создать игровое поле");
        newGame.setLocation(30, 20);
        newGame.setSize(200, 40);
        startFrame.add(newGame);

        final JButton go = new JButton("Начать новый забег");
        go.setLocation(30, 70);
        go.setSize(200, 40);
        startFrame.add(go);

        final JButton exit = new JButton("Выйти из игры");
        exit.setLocation(30, 120);
        exit.setSize(200, 40);
        startFrame.add(exit);

        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!gameBegin){
                    gameBegin = true;
                    startNewGame();
                }
            }
        });

        go.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (listCockroach.size() > 0){
                    restartGame();
                }
                else {
                    generateThreadsCockroach();
                }
                GamePause = false;
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                gameOver = true;
                System.exit(0);
            }
        });
    }

    private void threadGame(){
        Thread gameThread = new Thread(new Runnable() {
            public void run() {
                while (!gameOver) {
                    if(GameRestart){
                        outer: while (!killAll){
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                System.out.println("InterruptedException !");
                            }

                            for (CockroachRad cockroach : listCockroach) {
                                if (!cockroach.cockroachThreadDestroyed) {
                                    continue outer;
                                }
                            }

                            if(IsStartNewGame){
                                distributionByFinish();
                                IsStartNewGame = false;
                            }

                            System.out.println("Рестарт игры !");
                            killAll = true;
                        }

                        GameRestart = false;
                        generateThreadsCockroach();
                    }
                    else {
                        if (!GamePause){
                            gameCycle();
                            allCockroachesFinished();
                        }
                    }
                }
                System.out.println("Поток игры уничтожен !");
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void gameCycle() {
        message = GamePause ? "ПАУЗА !" : null;
        repaint();
    }

    public static void startNewGame() {
        JFrame frame = new JFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 235);
        frame.setBackground(new ColorUIResource(255, 255, 255));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.createBufferStrategy(2);

        JPanel commonPanel = new JPanel();
        commonPanel.setBackground(new ColorUIResource(200, 200, 200));
        commonPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        JPanel cockroachNameField = new CockroachNameFields();
        cockroachNameField.setPreferredSize(new Dimension(120, FieldHeight));
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        commonPanel.add(cockroachNameField, constraints);

        JPanel gamePanel = new Game();
        gamePanel.setPreferredSize(new Dimension(680, FieldHeight));
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 1;
        constraints.gridy = 0;
        commonPanel.add(gamePanel, constraints);

        JScrollPane scrPaneAll = new JScrollPane(commonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scrPaneAll);
    }

    public static void startNextGame() {
        IsStartNewGame = true;
        GameRestart = true;
        killAll = false;
    }

    public static void restartGame() {
        killAllCockroach();
        GameRestart = true;
        killAll = false;
    }

    private static void generateThreadsCockroach(){
        finishedCockroaches.clear();
        listCockroach.clear();
        int cockroachPosition = 0;
        Thread t[] = new Thread[CockroachNumber];
        for (int i = 0; i < t.length; i++) {
            String name = listCockroachName.get(i);
            if(name.trim().isEmpty()){
                name = "Таракан № " + (i + 1);
            }

            CockroachRad frog = new CockroachRad(0, cockroachPosition, i, name);
            cockroachPosition += CockroachThickness;
            listCockroach.add(frog);
            t[i] = new Thread(frog);
            t[i].setDaemon(true);
            t[i].setName(name);
            t[i].start();
        }
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(new ColorUIResource(0, 75, 50));
        graphics.fillRect(0, 0, FieldWidth, FieldHeight);

        graphics.setColor(new ColorUIResource(0, 150, 100));
        int y = 0;
        for (int i = 0; i <= CockroachNumber; i++) {
            graphics.drawLine(0, y, FieldWidth, y);
            y += 20;
        }

        graphics.setColor(new ColorUIResource(255, 0, 0));
        graphics.drawLine(FieldWidthWithoutIndent, 0, FieldWidthWithoutIndent, FieldHeight);

        if (!GamePause){
            drawCockroach(graphics);
        }

        graphics.setColor(new ColorUIResource(255, 255, 0));
        graphics.drawString("Лидирует: " + leadingCockroachName, FieldWidthWithoutIndent + 50, 10);

        if (message != null) {
            graphics.setColor(ColorUIResource.DARK_GRAY);
            graphics.fillRect(270, 270, 100, 30);
            graphics.setColor(ColorUIResource.RED);
            graphics.drawRect(270, 270, 100, 30);
            graphics.drawString(message, 280, 290);
        }

        identifyLeader();
    }

    private void drawCockroach(Graphics graphics){
        maxCoordinateX = -1;
        int leading = -1;

        for (int i = 0; i < listCockroach.size(); i++){
            CockroachRad cockroach = listCockroach.get(i);
            int positionByX = cockroach.getPositionByX();
            int positionByY = cockroach.getPositionByY();
            int cockroachId = cockroach.getId();
            String cockroachNum = String.valueOf((cockroachId + 1));

            graphics.setColor(new ColorUIResource(0, 255, 0));
            graphics.drawString("№ " + cockroachNum, FieldWidthWithoutIndent + 5, positionByY + 15);

            graphics.setColor(new ColorUIResource(255, 150, 100));
            graphics.fillOval(positionByX, positionByY, CockroachLength, CockroachThickness);

            if (maxCoordinateX < positionByX){
                maxCoordinateX = positionByX;
                leading = cockroachId;
            }

            graphics.setColor(new ColorUIResource(255, 255, 255));
            graphics.drawString(String.valueOf(cockroach.getName()), positionByX, positionByY + 15);

            if (compareCoordinates(cockroach)){
                cockroach.needKill = true;
            }
        }
        leadingCockroachID = leading;
    }

    private boolean compareCoordinates(CockroachRad frog){
        if ((frog.getPositionByX() > (FieldWidthWithoutIndent - CockroachLength))){
            killCockroachById(frog.getId());
            return true;
        }
        return false;
    }

    private void addDoubleClickMouseListener(){
        ActionListener al = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(singleClick){
                    System.out.println("Имитация второго клика !");
                    rushCockroach();
                }
            }
        };
        timer = new Timer(100, al);
        timer.setRepeats(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < listCockroach.size(); i++){
                    if (e.getX() > listCockroach.get(i).getPositionByX() && e.getX() < listCockroach.get(i).getPositionByX() + CockroachLength
                            && e.getY() > listCockroach.get(i).getPositionByY() && e.getY() < listCockroach.get(i).getPositionByY() + CockroachThickness){

                        if(e.getClickCount() == 2){
                            singleClick = true;
                            timer.start();
                            System.out.println("Второй клик подряд !");
                            hurriedCockroach = listCockroach.get(i);
                            rushCockroach();
                        }
                        else{
                            singleClick = false;
                        }

                        break;
                    }
                }
            }
        });
    }

    private static void killAllCockroach(){
        for (int i = 0; i < listCockroach.size(); i++) {
            listCockroach.get(i).needKill = true;
        }
    }

    public static void killCockroachById(int id){
        for (int i = 0; i < listCockroach.size(); i++) {
            CockroachRad cockroach = listCockroach.get(i);
            if (cockroach.getId() == id){
                listCockroach.remove(cockroach);
                return;
            }
        }
    }

    private void rushCockroach(){
        hurriedCockroach.setPositionByX(hurriedCockroach.getPositionByX() + 1);
        System.out.println("Поторопил таракана № " + hurriedCockroach.getId());
    }

    private void identifyLeader(){
        if (listCockroach.size() <= 0) return;

        for (int i = 0; i < listCockroach.size(); i++){
            int id = listCockroach.get(i).getId();
            if(id == leadingCockroachID){
                leadingCockroachName = String.valueOf(listCockroach.get(i).getName());
            }
        }
    }

    public void allCockroachesFinished(){
        if (!GamePause && listCockroach.size() == 0){
            startNextGame();
        }
    }

    private void distributionByFinish(){
        if (finishedCockroaches.size() > 3){
            System.out.println("Первым пришел: " + finishedCockroaches.get(0));
            System.out.println("Вторым пришел: " + finishedCockroaches.get(1));
            System.out.println("Третьим пришел: " + finishedCockroaches.get(2));
            for(int i=3; i < finishedCockroaches.size(); i++){
                int num = i + 1;
                System.out.println("Таракан по имени: " + finishedCockroaches.get(i) + " занял " + num + " место");
            }
        }
        else{
            for(int i=0; i<finishedCockroaches.size(); i++){
                System.out.println("Таракан по имени: " + finishedCockroaches.get(i) + " занял " + i + " место");
            }
        }
    }

}
