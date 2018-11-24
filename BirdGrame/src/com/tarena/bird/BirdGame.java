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

    /**游戏状态*/
    int state;
    public static final int START = 0;
    public static final int RUNNING = 1;
    public static final int GAMEOVER = 2;
    BufferedImage gameOverImage;
    BufferedImage startImage;
    int score; //分数

    /**初始化BirdGame的属性变量*/
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

    /**重写paint方法实现绘制*/
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
        //在paint方法中添加显示游戏结束的状态代码
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
                            bird.flappy(); //鸟向上飞扬
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        };

        //将l挂在当前的面板（game）上
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
                    bird.step();//上下移动
                    bird.fly();//挥动翅膀
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

    /**启动软件的方法*/
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



/**地面*/
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

    //地面的类体中，添加方法，地面移动一步
    public void step(){
        x--;
        if(x==-70){
            x=0;
        }
    }
}



/**柱子类型，x,y是柱子的中心点的位置*/
class Column{
    BufferedImage image;
    int x,y;
    int width,height;
    /**柱子中间的缝隙*/
    int gap;
    int distance; //两个柱子之间的距离
    Random random = new Random();

    /**构造器：初始化数据，n代表第几个柱子*/
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



/**鸟类型，x,y是鸟的中心点的位置*/
class Bird{
    BufferedImage image;
    int x,y;
    int width,height;
    int size;  //鸟的大小，用于碰撞检测

    //增加属性，用于计算鸟的位置
    double g; //重力加速度
    double t; //两次位置的间隔时间
    double v0; //初始上抛速度
    double speed; //当前的上抛速度
    double s;  //经过时间t以后的位移
    double alpha; //鸟的倾角 弧度单位
     /**在Bird类中定义一组（数组）图片，是鸟的动画帧*/
     BufferedImage[] images;
     int index; //动画帧数组元素的下标位置
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

    //飞翔（fly）
    public void fly(){
        index++;
        image = images[(index/12)%8];
    }

    //鸟的移动方法
    public void step(){
        double v0 = speed;
        s = v0 * t + g * 2 * 2 / 2; //计算上抛位移
        y = y - (int) s;  //计算鸟的坐标位置
        double v = v0 - g * t;  //计算下次的速度
        speed = v;
        alpha = Math.atan(s/8);  //调用Java API 提供的反正切函数，计算倾角
    }

    public void flappy(){  //重新设置速度，重新向上飞
        speed = 0;
    }
    /**
     * 在鸟中添加方法hit，检测当前鸟是否碰到地面ground，如果返回true表示发生碰撞
     */
    public boolean hit(Ground ground){
        boolean hit = y + size /2 > ground.y;
        if(hit){
            y = ground.y  - size / 2;
            alpha = -3.14159265358979323 / 2;
        }
        return hit;
    }

    //检测当前的鸟是否撞到柱子
    public boolean hit(Column column){
        //先检测是否在柱子的范围以内
        if(x>column.x - column.width/2 - size/2 && x<column.x + column.width/2 + size/2){
            if(y>column.y - column.gap/2 + size /2 && y<column.y +column.gap /2 - size/2){
                return false;
            }
            return true;
        }
        return false;
    }
}
