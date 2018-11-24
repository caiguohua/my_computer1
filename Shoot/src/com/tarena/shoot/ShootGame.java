package com.tarena.shoot;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import static javax.imageio.ImageIO.read;

public class ShootGame extends JPanel {
    public static final int WIDTH =400;  //����
    public static final int HEIGHT =654; //����

    /**��Ϸ�ĵ�ǰ״̬��START RUNNING PAUSE GAME OVER */
    private int state;
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int PAUSE = 2;
    public static final int GAME_OVER = 3;

    private int score = 0;   //�÷�
    private Timer timer;   //��ʱ��
    private int intervel = 1000/100;  //ʱ���������룩

    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;
    public static BufferedImage pause;
    public static BufferedImage gameover;


    private FlyingObject[] flyings = {};  //�л�����
    private Bullet[] bullets = {};        //�ӵ�����
    private Hero hero = new Hero();       //Ӣ�ۻ�

    int flyEnteredIndex = 0; //�������볡����
    int shootIndex = 0;  //�������


    public ShootGame(){
        flyings = new FlyingObject[2]; //��ʼ��һֻ�۷�һ�ܷɻ�
        flyings[0] = new Airplane();
        flyings[1] = new Bee();
        bullets = new Bullet[1]; //��ʼ��һ���ӵ�
        bullets[0] = new Bullet(200,350);
    }

    static{   //��̬�����
        try{
            background = read(ShootGame.class.getResource("background.png"));
            airplane = read(ShootGame.class.getResource("airplane.png"));
            bee = read(ShootGame.class.getResource("bee.png"));
            bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
            hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
            hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
            pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
            start = ImageIO.read(ShootGame.class.getResource("start.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(background,0,0,null);  //������ͼ
        paintHero(g);  //��Ӣ�ۻ�
        paintBullets(g);  //���ӵ�
        paintFlyingObjects(g);   //��������
        paintScore(g);    //������
        paintState(g);   //����Ϸ״̬
    }

    /**��Ӣ�ۻ�*/
    public void paintHero(Graphics g){
        g.drawImage(hero.getImage(),hero.getX(),hero.getY(),null);
    }
    /**���ӵ�*/
    public void paintBullets(Graphics g){
        for(int i = 0;i<bullets.length;i++){
            Bullet b = bullets [i];
            g.drawImage(b.getImage(),b.getX(),b.getY(),null);
        }
    }
    /** ��������*/
    public void paintFlyingObjects(Graphics g){
        for(int i =0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            g.drawImage(f.getImage(),f.getX(),f.getY(),null);
        }
    }

    /**������*/
    public void paintScore(Graphics g){
        int x =10;
        int y =25;
        Font font =new Font(Font.SANS_SERIF,Font.BOLD,14);
        g.setColor(new Color(0x3A3B3B));
        g.setFont(font);
        g.drawString("SCORE:"+score,x,y);
        y +=20;
        g.drawString("LIFE:"+hero.getLife(),x,y);
    }

    /**����Ϸ״̬*/
    public void paintState(Graphics g){
        switch (state){
            case START:
                g.drawImage(start,0,0,null);
                break;
            case PAUSE:
                g.drawImage(pause,0,0,null);
                break;
            case GAME_OVER:
                g.drawImage(gameover,0,0,null);
                break;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fly");
        ShootGame game = new ShootGame();  //������
        frame.add(game);
        frame.setSize(WIDTH,HEIGHT);  //��С
        frame.setAlwaysOnTop(true);  //����������
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Ĭ�Ϲرղ���
        frame.setLocationRelativeTo(null);  //���ô����ʼλ��
        frame.setVisible(true);  //�������paint
        game.action();  //����ִ��
    }

    public void action(){  //����ִ�д���
        //�������¼�
        MouseAdapter l = new MouseAdapter(){
            @Override
            public void mouseMoved(MouseEvent e) {  //����ƶ�
                if(state == RUNNING){  //����ʱ�ƶ�Ӣ�ۻ�
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x,y);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {  //������
                if(state == PAUSE){  //��ͣʱ����
                    state = RUNNING;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) { //����˳�
                if(state != GAME_OVER){
                    state = PAUSE; //��Ϸδ��������������Ϊ��ͣ
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {  //�����
                switch (state){
                    case START:
                        state = RUNNING;
                        break;
                    case GAME_OVER:   //��Ϸ����
                        flyings = new FlyingObject[0];
                        bullets = new Bullet[0];
                        hero = new Hero();
                        score = 0;
                        state = START;
                        break;
                }
            }
        };
        this.addMouseListener(l); //�������������
        this.addMouseMotionListener(l); //������껬������

        timer = new Timer();  //�����̿���
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                if (state == RUNNING) {
                    enterAction();  //�������볡
                    stepAction();  //��һ��
                    shootAction(); //���
                    bangAction();  //�ӵ��������
                    outOfBoundsAction();  //ɾ��Խ������Ｐ�ӵ�
                    checkGameOverAction();  //�����Ϸ����
                }
                repaint();  //�ػ�
            }
        },intervel,intervel);
    }


    /**
     * ������ɷ�����
     * @param args
     */
    public static FlyingObject nextOne(){
        Random random = new Random();
        int type = random.nextInt(20);
        if(type==0){
            return new Bee();
        }else {
            return new Airplane();
        }
    }

    /**�������볡*/
    public void enterAction(){
        flyEnteredIndex++;
        if(flyEnteredIndex % 40 ==0){
            FlyingObject obj = nextOne();  //�������һ��������
            flyings = Arrays.copyOf(flyings,flyings.length+1);
            flyings[flyings.length-1] = obj;
        }
    }

    public void stepAction(){
        /**��������һ��*/
        for(int i = 0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            f.step();
        }

        /**�ӵ���һ��*/
        for(int i =0;i<bullets.length;i++){
            Bullet b = bullets[i];
            b.step();
        }
        hero.step();
    }

    /**���*/
    public void shootAction(){
        shootIndex++;
        if(shootIndex % 30 ==0){  //100���뷢һ��
            Bullet[] bs = hero.shoot();  //Ӣ�۴���ӵ�
            bullets = Arrays.copyOf(bullets,bullets.length+bs.length);
            System.arraycopy(bs,0,bullets,bullets.length-bs.length,bs.length);
        }
    }

    /**�ӵ����������ײ���*/
    public void bangAction(){
        for(int i = 0;i < bullets.length; i++){
            Bullet b = bullets[i];
            bang(b);
        }
    }

    /**�ӵ��������֮�����ײ���*/
    public void bang(Bullet bullet){
        int index = -1;
        for(int i = 0; i<flyings.length;i++){
            FlyingObject obj = flyings[i];
            if(obj.shootBy(bullet)){
                index = i;
                break;
            }
        }
        if(index != -1){
            FlyingObject one = flyings[index];  //��¼�����еķ�����
            FlyingObject temp = flyings[index];  //�����еķ����������һ�������ｻ��
            flyings[index] = flyings[flyings.length-1];
            flyings[flyings.length-1] = temp;
            flyings = Arrays.copyOf(flyings, flyings.length-1); //ɾ�����һ��������������еģ�

            //���one������ ����ǵ��ˣ������
            if(one instanceof Enemy){  //������ͣ��ǵ��ˣ���ӷ�
                Enemy e =(Enemy) one;
                score += e.getScore();
            }
            if(one instanceof Award){  //��Ϊ����
                Award a = (Award) one;
                int type = a.getType();
                switch (type){
                    case Award.DOUBLE_FIRE:
                        hero.addDoubleFire();
                        break;
                    case Award.LIFE:
                        hero.addLife();
                        break;
                }
            }
        }
    }

    /**ɾ��Խ������Ｐ�ӵ�*/
    public void outOfBoundsAction(){
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];
        for(int i =0;i<flyings.length;i++){
            FlyingObject f = flyings[i];
            if(!f.outOfBounds()){
                flyingLives[index++] = f;  //��Խ�������
            }
        }
        flyings = Arrays.copyOf(flyingLives,index);

        index = 0;
        Bullet[] bulletLives = new Bullet[bullets.length];
        for(int  i =0;i<bullets.length;i++){
            Bullet b= bullets[i];
            if(!b.outOfBounds()){
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives,index);
    }

    /**�����Ϸ����*/
    public void checkGameOverAction(){
        if(isGameOver()){
            state = GAME_OVER;
        }
    }

    /**�����Ϸ�Ƿ����*/
    public boolean isGameOver(){
        for(int i = 0;i<flyings.length;i++){
            int index = -1;
            FlyingObject obj =flyings[i];
            if(hero.hit(obj)){
                hero.subtractLife();
                hero.setDoubleFire(0);
                index = i;
            }
            if(index != -1){
                FlyingObject t =flyings[index];
                flyings[index] = flyings[flyings.length-1];
                flyings[flyings.length-1] = t;
                flyings = Arrays.copyOf(flyings, flyings.length-1);
            }
        }
        return hero.getLife() <= 0;
    }
}
