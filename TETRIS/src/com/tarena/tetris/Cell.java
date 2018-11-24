package com.tarena.tetris;

import java.awt.image.BufferedImage;

public class Cell {
    private int row; //行
    private int col; //列
    private BufferedImage image;  //格子的贴图

    public Cell(int row, int col, BufferedImage image) {
        super();
        this.row = row;
        this.col = col;
        this.image = image;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void drop(){
        row++;
    }

    public void moveRight(){
        col++;
    }

    public void moveLift(){
        col--;
    }

    @Override
    public String toString() {
        return row+","+col;
    }
}
