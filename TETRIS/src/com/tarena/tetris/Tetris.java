package com.tarena.tetris;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Tetris extends JPanel {
    private int state;
    public static final int RUNNING = 0;
    public static final int PAUSE = 1;
    public static final int GAME_OVER = 2;

    private int score; //����
    private int lines; //���ٵ�����
    private Cell[][] wall; //����ǽ
    private Tetromino tetromino; //����������ĸ񷽿�
    private Tetromino nextOne; //��һ���ĸ񷽿�
    /**����ͼƬ*/
    private static BufferedImage background;
    private static BufferedImage gameover;
    private static BufferedImage pause;
    public static BufferedImage T;
    public static BufferedImage S;
    public static BufferedImage I;
    public static BufferedImage L;
    public static BufferedImage J;
    public static BufferedImage O;
    public static BufferedImage Z;
    public static final int ROWS = 20;   //����ǽ������
    public static final int COLS = 10;  //����ǽ������

    /**��Tetris�������Ӷ�ʱ��*/
    private Timer timer;

    /**�ٶ�*/
    private int speed;

    /**�Ѷȼ���*/
    private int level;

    /**��������� �� index % speed == 0 ʱ������һ��*/
    private int index;

    /**ʹ�þ�̬�������ؾ�̬��ͼƬ*/
    static {
        try{
            background = ImageIO.read(Tetris.class.getResource("tetris.png"));
            gameover = ImageIO.read(Tetris.class.getResource("gameover.png"));
            pause = ImageIO.read(Tetris.class.getResource("pause.png"));
            T = ImageIO.read(Tetris.class.getResource("T.png"));
            I = ImageIO.read(Tetris.class.getResource("I.png"));
            S = ImageIO.read(Tetris.class.getResource("S.png"));
            Z = ImageIO.read(Tetris.class.getResource("Z.png"));
            J = ImageIO.read(Tetris.class.getResource("J.png"));
            L = ImageIO.read(Tetris.class.getResource("L.png"));
            O = ImageIO.read(Tetris.class.getResource("O.png"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * JPanel paint() paint �� ��д paint�����޸�ԭ�еĻ��Ʒ���
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(background,0,0,null);
        g.translate(15,15);
        paintWall(g);
        paintTetromino(g);
        paintNextOne(g);
        paintScore(g);
        paintState(g);
    }

    private void paintState(Graphics g){
        switch (state){
            case PAUSE:
                g.drawImage(pause,-15,-15,null);
                break;
            case GAME_OVER:
                g.drawImage(gameover,-15,-15,null);
                break;
        }
    }

    public void action(){
        wall = new Cell[ROWS][COLS];
        tetromino = Tetromino.randomOne();
        nextOne = Tetromino.randomOne();
        state = RUNNING;
        KeyAdapter l = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (state){
                    case GAME_OVER:
                        processGameoverKey(key);
                        break;
                    case PAUSE:
                        processPauseKey(key);
                        break;
                    case RUNNING:
                        processRunningKey(key);
                }
                repaint();
            }
        };

        //���¼�����ǰ���
        this.requestFocus();
        this.addKeyListener(l);

        //��Action��������ӣ���ʱ�ƻ�����
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                speed = 40 - (lines / 100);
                speed = speed< 1 ? 1 : speed;
                level = 41 -speed;
                if(state == RUNNING && index % speed ==0){
                    softDropAction();
                }
                index++;
                repaint();
            }
        },10,10);
    }

    private void processPauseKey(int key){
        switch (key){
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_C:
                index = 0;
                state = RUNNING;
                break;
        }
    }

    protected void processRunningKey(int key){
        switch (key){
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_RIGHT:
                Tetris.this.moveRightAction();
                break;
            case KeyEvent.VK_LEFT:
                Tetris.this.moveLeftAction();
                break;
            case KeyEvent.VK_DOWN:
                softDropAction();
                break;
            case KeyEvent.VK_SPACE:
                hardDropAction();
                break;
            case KeyEvent.VK_UP:
                rotateRightAction();
                break;
            case KeyEvent.VK_P:
                state = PAUSE;
                break;
        }
    }

    protected void processGameoverKey(int key){
        switch (key){
            case KeyEvent.VK_Q:
                System.exit(0);
                break;
            case KeyEvent.VK_S:
                /**��Ϸ���¿�ʼ*/
                this.lines = 0;
                this.score = 0;
                this.wall = new Cell[ROWS][COLS];
                this.tetromino = Tetromino.randomOne();
                this.nextOne = Tetromino.randomOne();
                this.state = RUNNING;
                this.index = 0;
                break;
        }
    }

    public static final int FONT_COLOR = 0x667799;
    public static final int FONT_SIZE = 30;

    /**
     * ���Ʒ���
     */
    private void paintScore(Graphics g){
        int x = 290;
        int y = 160;
        g.setColor(new Color(FONT_COLOR));
        Font font = g.getFont();
        font = new Font(font.getName(),font.getStyle(),FONT_SIZE);
        g.setFont(font);
        String str = "SCORE:" + score;
        g.drawString(str,x,y);
        y += 56;
        str = "LINES:"+lines;
        g.drawString(str,x,y);
        y +=56;
        g.drawString("LEVEL:"+level,x,y);
    }

    /**
     * ������һ��Ҫ����ķ���
     */
    private void paintNextOne(Graphics g){
        if(nextOne == null){
            return;
        }
        Cell[] cells = nextOne.cells;
        for(int i = 0;i<cells.length;i++){
            Cell cell = cells[i];
            int x = (cell.getCol() + 10) * CELL_SIZE;
            int y = (cell.getRow() + 1) * CELL_SIZE;
            g.drawImage(cell.getImage(),x-1,y-1,null);
        }
    }

    /**
     * ������������ķ���
     */
    public void paintTetromino(Graphics g){
        if(tetromino == null){
            return;
        }
        Cell[] cells = tetromino.cells;
        for(int i = 0;i<cells.length;i++){
            Cell cell = cells[i];
            int x = cell.getCol() * CELL_SIZE;
            int y = cell.getRow() * CELL_SIZE;
            g.drawImage(cell.getImage(),x-1,y-1,null);
        }
    }

    public static final int CELL_SIZE = 26;

    /**��ǽ*/
    private void paintWall(Graphics g){
        for(int row = 0;row<wall.length;row++){
            Cell[] line = wall[row];
            for(int col = 0;col<line.length;col++){
                Cell cell = line[col];
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                if(cell == null){
                    g.drawRect(x,y,CELL_SIZE,CELL_SIZE);
                }else{
                    g.drawImage(cell.getImage(),x-1,y-1,null);
                }
            }
        }
    }

    /**��鵱ǰ��������ķ����Ƿ������*/
    private boolean outOfBounds(){
        Cell[] cells = tetromino.cells;
        for(int i=0;i<cells.length;i++){
            Cell cell = cells[i];
            int col = cell.getCol();
            if(col<0||col>=COLS){
                return true;
            }
        }
        return false;
    }

    /**�����������ķ����Ƿ���ǽ�ϵ�ש���ص�*/
    private boolean coincide(){
        Cell[] cells = tetromino.cells;
        for(int i=0;i<cells.length;i++){
            Cell cell =cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            if(row >= 0 && row < ROWS && col >= 0 && col <= COLS && wall[row][col] != null){
                return true;
            }
        }
        return false;
    }

    /**��Tetris������ӷ����������ƶ������̿���*/
    public void moveRightAction(){
        //���������ƶ���������ֳ����˱߽磬�������ƶ�����������
        tetromino.moveRight();
        if(outOfBounds() || coincide()){
            tetromino.moveLeft();
        }
    }

    public void moveLeftAction(){
        tetromino.moveLeft();
        if(outOfBounds() || coincide()){
            tetromino.moveRight();
        }
    }

    /**�������̿���*/
    public void softDropAction(){
        if(canDrop()){
            tetromino.softDrop();
        }else{
            landIntoWall();
            destoryLines();
            if(isGameOver()){
                state = GAME_OVER;
            }else{
                tetromino = nextOne;
                nextOne = Tetromino.randomOne();
            }
        }
    }

    private static int[] scoreTable ={0,1,10,50,100};

    private void destoryLines(){
        int lines = 0;
        for(int row = 0;row<wall.length;row++){
            if(fullCells(row)){
                deleteRow(row);
                lines++;
            }
        }
        this.score += scoreTable[lines];
        this.lines += lines;
    }

    private void deleteRow(int row){
        for(int i = row;i >= 1;i--){
            System.arraycopy(wall[i-1],0,wall[i],0,COLS);
        }
        Arrays.fill(wall[0],null);
    }

    /**
     * ��鵱ǰ�е�ÿ�����ӣ��Ƿ������ģ���������򷵻�true�����򷵻���false
     */
    private boolean fullCells(int row){
        Cell[] line = wall[row];
        for(Cell cell : line){
            if(cell == null){
                return false;
            }
        }
        return true;
    }

    private void landIntoWall(){
        Cell[] cells = tetromino.cells;
        for(int i=0;i<cells.length;i++){
            Cell cell = cells[i];
            int row = cell.getRow();
            int col = cell.getCol();
            wall[row][col] = cell;
        }
    }

    /**��鵱ǰ�ķ����Ƿ��ܹ����䣬����true�ܹ�����*/
    private boolean canDrop(){
        Cell[] cells = tetromino.cells;
        for(int i=0;i<cells.length;i++){
            Cell cell = cells[i];
            int row = cell.getRow();
            if(row == ROWS - 1){
                return false;
            }
        }
        for(Cell cell : cells){
            int row = cell.getRow() + 1;
            int col = cell.getCol();
            if(row >= 0 && row < ROWS && col >= 0 && col <= COLS && wall[row][col] != null){
                return false;
            }
        }
        return true;
    }

    /**
     * Ӳ�������̣����䵽��������Ϊֹ�󶨵��ո�VK_SPACE)�¼���
     */
    public void hardDropAction(){
        while(canDrop()){
            tetromino.softDrop();
        }
        landIntoWall();
        destoryLines();
        if(isGameOver()){
            state = GAME_OVER;
        }else{
            tetromino = nextOne;
            nextOne = Tetromino.randomOne();
        }
    }

    public void rotateRightAction(){
        tetromino.rotateRight();
        if(outOfBounds() || coincide()){
            tetromino.rotateLeft();
        }
    }

    /**�����Ϸ�Ƿ����*/
    private boolean isGameOver(){
        Cell[] cells = nextOne.cells;
        for(Cell cell : cells){
            int row = cell.getRow();
            int col = cell.getCol();
            if(wall[row][col] != null){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Tetris tetris = new Tetris();
        tetris.setBackground(new Color(0x0000ff));
        frame.add(tetris);
        frame.setSize(530,580);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        tetris.action();
    }
}
