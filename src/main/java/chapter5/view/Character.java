package chapter5.view;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.concurrent.TimeUnit;

import chapter5.Launcher;
import chapter5.model.AnimatedSprite;

public class Character extends Pane {
    public static final int CHARACTER_WIDTH = 32;
    public static final int CHARACTER_HEIGHT = 64;
    private Image characterImg;
    private AnimatedSprite imageView;
    private int x;
    private int y;
    private int startX;
    private int startY;
    private int offsetX;
    private int offsetY;
    private int score = 0;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode upKey;
    int xVelocity = 0;
    int yVelocity = 0;
    int xAcceleration = 1;
    int yAcceleration = 1;
    int xMaxVelocity = 7;
    int yMaxVelocity = 17;
    boolean isMoveLeft = false;
    boolean isMoveRight = false;
    boolean isFalling = true;
    boolean canJump = false;
    boolean isJumping = false;

    public Character(int x, int y, int offsetX, int offsetY, KeyCode leftKey, KeyCode rightKey, KeyCode upKey) {
        this.startX = x;
        this.startY = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.x = x;
        this.y = y;
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.characterImg = new Image(Launcher.class.getResourceAsStream("assets/MarioSheet.png"));
        this.imageView = new AnimatedSprite(characterImg,4,4,1,offsetX,offsetY,16,32);
        this.imageView.setFitWidth(CHARACTER_WIDTH);
        this.imageView.setFitHeight(CHARACTER_HEIGHT);
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.upKey = upKey;
        this.getChildren().addAll(this.imageView);
    }
    public void moveLeft() {
        isMoveLeft = true;
        isMoveRight = false;
    }
    public void moveRight() {
        isMoveLeft = false;
        isMoveRight = true;
    }
    public void stop() {
        isMoveLeft = false;
        isMoveRight = false;
    }
    public void moveX() {
        setTranslateX(x);
        if(isMoveLeft) {
            xVelocity = xVelocity>=xMaxVelocity? xMaxVelocity : xVelocity+xAcceleration;
            x = x - xVelocity;
        }
        if(isMoveRight) {
            xVelocity = xVelocity>=xMaxVelocity? xMaxVelocity : xVelocity+xAcceleration;
            x = x + xVelocity;
        }
    }
    public void moveY() {
        setTranslateY(y);
        if(isFalling) {
            yVelocity = yVelocity >= yMaxVelocity? yMaxVelocity : yVelocity+yAcceleration;
            y = y + yVelocity;
            //System.out.println("falling");
        } else if(isJumping) {
            yVelocity = yVelocity <= 0 ? 0 : yVelocity-yAcceleration;
            y = y - yVelocity;
            //System.out.println("jumping");
        }
    }
    public void checkReachGameWall() {
        if(x <= 0) {
            x = 0;
        } else if( x >= Platform.WIDTH-CHARACTER_WIDTH) {
            x = Platform.WIDTH-CHARACTER_WIDTH;
            //System.out.println(x);
        }
    }
    public void jump() {
        if (canJump) {
            yVelocity = yMaxVelocity;
            canJump = false;
            isJumping = true;
            isFalling = false;
        }
    }
    public void checkReachHighest () {
        if(isJumping && yVelocity <= 0) {
            isJumping = false;
            isFalling = true;
            yVelocity = 0;
        }
    }
    public void checkReachFloor() {
        if(isFalling && y >= Platform.GROUND - CHARACTER_HEIGHT) {
            isFalling = false;
            canJump = true;
            yVelocity = 0;
            //System.out.println(getY());
        }
    }
    public void repaint() {
        moveX();
        moveY();
    }
    public KeyCode getLeftKey() {
        return leftKey;
    }
    public KeyCode getRightKey() {
        return rightKey;
    }
    public KeyCode getUpKey() {
        return upKey;
    }
    public AnimatedSprite getImageView() {
        return imageView;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getOffsetX(){
        return offsetX;
    }
    public int getOffsetY(){
        return offsetY;
    }
    public int getScore(){
        return score;
    }

    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }

    public void collided(Character c){
        if(isMoveLeft){
            x = c.getX() + CHARACTER_WIDTH + 1;
            stop();
        } else if(isMoveRight){
            x = c.getX() - CHARACTER_WIDTH + 1;
            stop();
        }
        if(y < Platform.GROUND - CHARACTER_HEIGHT){
            if(isFalling && y < c.getY() && Math.abs(y-c.getY()) <= CHARACTER_HEIGHT+1){
                score++;
                y = Platform.GROUND - CHARACTER_HEIGHT - 5;
                //System.out.println(y);
                repaint();
                c.collapsed();
                c.respawn();
            }
        }
    }

    private void collapsed() {
        imageView.setFitHeight(5);
        y = Platform.GROUND - 5;
        repaint();
        try{
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public void respawn(){
        x = startX;
        y = startY;
        imageView.setFitWidth(CHARACTER_WIDTH);
        imageView.setFitHeight(CHARACTER_HEIGHT);
        isMoveLeft = false;
        isMoveRight = false;
        isFalling = true;
        canJump = false;
        isJumping = false;
    }
}