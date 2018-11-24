package com.tarena.shoot;
/**子弹类：是飞行物*/
public class Bullet extends FlyingObject{
    private int speed = 3;  //移动的速度

    public Bullet(int x,int y) {
        this.x = x;
        this.y = y;
        this.image = ShootGame.bullet;
    }

    @Override
    public void step() {
        y -= speed;
    }

    @Override
    public boolean outOfBounds(){
        return y<-height;
    }
}
