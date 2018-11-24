package com.tarena.bird;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BirdGame extends JPanel {
    Bird bird;
    Column column1,column2;
    Ground ground;
    BufferedImage background;

    /**��Ϸ״̬*/
    int state;
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int GAMEOVER = 2;
    BufferedImage gameOverImage;
    BufferedImage startImage;
    int score; //����

    /**��ʼ��BirdGame�����Ա���*/
    public BirdGame() throws Exception{
        state = START;
        startImage = ImageIO.read(getClass().getResource("start.png"));
        gameOverImage = ImageIO.read(getClass().getResource("gameover.png"));
        score = 0;
        bird = new Bird();
        column1 = new Column(1);
        column2 = new Column(2);
        ground = new Ground();
        background = ImageIO.read(getClass().getResource("bg.png"));
    }

    /**��дpaint����ʵ�ֻ���*/
    public void paint(Graphics g){
        g.drawImage(background,0,0,null);
        g.drawImage(column1.image,column1.x - column1.width / 2,column1.y - column1.height / 2,null);
        g.drawImage(column2.image,column2.x - column2.width / 2,column2.y - column2.height /2,null);
        Font f =new Font(Font.SANS_SERIF,Font.BOLD,40);
        g.setFont(f);
        g.drawString(""+score,40,60);
        g.setColor(Color.WHITE);
        g.drawString(""+score,40 - 3,60 - 3);

        g.drawImage(ground.image,ground.x,ground.y,null);
        Graphics2D g2 = (Graphics2D) g;
        g2.rotate(-bird.alpha,bird.x,bird.y);
        g.drawImage(bird.image,bird.x - bird.width / 2,bird.y - bird.height / 2,null);
        g2.rotate(bird.alpha,bird.x,bird.y);
        //��paint�����������ʾ��Ϸ������״̬����
        switch (state){
            case GAMEOVER:
                g.drawImage(gameOverImage,0,0,null);
                break;
            case START:
                g.drawImage(startImage,0,0,null);
                break;
        }
    }


    public void action() throws Exception{
        MouseListener l = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try{
                    switch (state){
                        case GAMEOVER:
                            column1 = new Column(1);
                            column2 = new Column(2);
                            bird = new Bird();
                            score = 0;
                            state = START;
                            break;
                        case START:
                            state = RUNNING;
                        case RUNNING:
                            bird.flappy(); //�����Ϸ���
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        //��l���ڵ�ǰ����壨game����
        addMouseListener(l);

        while (true){
            switch (state){
                case START:
                    bird.fly();
                    ground.step();
                    break;
                case RUNNING:
                    column1.step();
                    column2.step();
                    bird.step();//�����ƶ�
                    bird.fly();//�Ӷ����
                    ground.step();
                    if(bird.x == column1.x || bird.x == column2.x){
                        score++;
                    }
                    if (bird.hit(ground) || bird.hit(column1) || bird.hit(column2)){
                        state = GAMEOVER;
                    }
                    break;
            }
            repaint();
            Thread.sleep(1000/30);
        }
    }

    /**��������ķ���*/
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        BirdGame game = new BirdGame();
        frame.add(game);
        frame.setSize(440,670);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.action();
    }
}



/**����*/
class Ground{
    BufferedImage image;
    int x,y;
    int width,height;

    public Ground() throws Exception{
        image = ImageIO.read(getClass().getResource("ground.png"));
        width = image.getWidth();
        height = image.getHeight();
        x=0;
        y=500;
    }

    //����������У���ӷ����������ƶ�һ��
    public void step(){
        x--;
        if(x==-70){
            x=0;
        }
    }
}



/**�������ͣ�x,y�����ӵ����ĵ��λ��*/
class Column{
    BufferedImage image;
    int x,y;
    int width,height;
    /**�����м�ķ�϶*/
    int gap;
    int distance; //��������֮��ľ���
    Random random = new Random();

    /**����������ʼ�����ݣ�n����ڼ�������*/
    public Column(int n) throws Exception{
        image = ImageIO.read(getClass().getResource("column.png"));
        width = image.getWidth();
        height = image.getHeight();
        gap = 144;
        distance = 245;
        x = 550+(n-1)*distance;
        y = random.nextInt(218)+132;
    }

    public void step(){
        x--;
        if(x == -width /2){
            x = distance * 2 - width / 2;
            y = random.nextInt(218) + 132;
        }
    }
}



/**�����ͣ�x,y��������ĵ��λ��*/
class Bird{
    BufferedImage image;
    int x,y;
    int width,height;
    int size;  //��Ĵ�С��������ײ���

    //�������ԣ����ڼ������λ��
    double g; //�������ٶ�
    double t; //����λ�õļ��ʱ��
    double v0; //��ʼ�����ٶ�
    double speed; //��ǰ�������ٶ�
    double s;  //����ʱ��t�Ժ��λ��
    double alpha; //������ ���ȵ�λ
     /**��Bird���ж���һ�飨���飩ͼƬ������Ķ���֡*/
     BufferedImage[] images;
     int index; //����֡����Ԫ�ص��±�λ��
    public Bird() throws Exception{
        image = ImageIO.read(getClass().getResource("0.png"));
        width = image.getWidth();
        height = image.getHeight();
        x=132;
        y=280;
        size=40;
        g=1;
        v0=20;
        t=0.25;
        speed=v0;
        s=0;
        alpha=0;
        images = new BufferedImage[8];
        for(int i=0;i<8;i++){
            images[i] = ImageIO.read(getClass().getResource(i+".png"));
        }
        index = 0;
    }

    //���裨fly��
    public void fly(){
        index++;
        image = images[(index/12)%8];
    }

    //����ƶ�����
    public void step(){
        double v0 = speed;
        s = v0 * t + g * 2 * 2 / 2; //��������λ��
        y = y - (int) s;  //�����������λ��
        double v = v0 - g * t;  //�����´ε��ٶ�
        speed = v;
        alpha = Math.atan(s/8);  //����Java API �ṩ�ķ����к������������
    }

    public void flappy(){  //���������ٶȣ��������Ϸ�
        speed = 0;
    }
    /**
     * ��������ӷ���hit����⵱ǰ���Ƿ���������ground���������true��ʾ������ײ
     */
    public boolean hit(Ground ground){
        boolean hit = y + size /2 > ground.y;
        if(hit){
            y = ground.y  - size / 2;
            alpha = -3.14159265358979323 / 2;
        }
        return hit;
    }

    //��⵱ǰ�����Ƿ�ײ������
    public boolean hit(Column column){
        //�ȼ���Ƿ������ӵķ�Χ����
        if(x>column.x - column.width/2 - size/2 && x<column.x + column.width/2 + size/2){
            if(y>column.y - column.gap/2 + size /2 && y<column.y +column.gap /2 - size/2){
                return false;
            }
            return true;
        }
        return false;
    }
}
