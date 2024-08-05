package chapter5;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import chapter5.controller.DrawingLoop;
import chapter5.controller.GameLoop;
import chapter5.view.Character;
import chapter5.view.Platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CharacterTest {
    private Character floatingCharacter, 
                        floatingSecondCharacter1,
                        stompingSecondCharacter2, 
                        standingFirstCharacter3,
                        standingSecondCharacter4,
                        borderCharacter5;
    private ArrayList<Character> characterListUnderTest;
    private Platform platformUnderTest;
    private GameLoop gameLoopUnderTest;
    private DrawingLoop drawingLoopUnderTest;
    private Method updateMethod, redrawMethod;

    @Before
    public void setup(){
        JFXPanel jfxPanel = new JFXPanel();
        floatingCharacter = new Character(30, 30, 0, 0, KeyCode.A, KeyCode.D,KeyCode.W);
        floatingSecondCharacter1 = new Character(Platform.WIDTH-60, 30,0,96, KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        stompingSecondCharacter2 = new Character(30, 29, 0, 0, KeyCode.A, KeyCode.D,KeyCode.W);
        standingFirstCharacter3 = new Character(0, Platform.GROUND-Character.CHARACTER_HEIGHT, 0, 0, KeyCode.A, KeyCode.D,KeyCode.W);
        standingSecondCharacter4 = new Character(Character.CHARACTER_WIDTH-1, Platform.GROUND-Character.CHARACTER_HEIGHT, 0, 0, KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        borderCharacter5 = new Character(Platform.WIDTH-Character.CHARACTER_WIDTH, Platform.GROUND-Character.CHARACTER_HEIGHT, 0, 0, KeyCode.LEFT,KeyCode.RIGHT,KeyCode.UP);
        characterListUnderTest = new ArrayList<Character>();
        characterListUnderTest.add(floatingCharacter);
        characterListUnderTest.add(floatingSecondCharacter1);
        characterListUnderTest.add(stompingSecondCharacter2);
        characterListUnderTest.add(standingFirstCharacter3);
        characterListUnderTest.add(standingSecondCharacter4);
        characterListUnderTest.add(borderCharacter5);
        platformUnderTest = new Platform();
        gameLoopUnderTest = new GameLoop(platformUnderTest);
        drawingLoopUnderTest = new DrawingLoop(platformUnderTest);
        try {
            updateMethod = GameLoop.class.getDeclaredMethod("update", ArrayList.class);
            redrawMethod = DrawingLoop.class.getDeclaredMethod("paint", ArrayList.class);
            updateMethod.setAccessible(true);
            redrawMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            updateMethod = null;
            redrawMethod = null;
        }
    }

    @Test
    public void characterInitialValuesShouldMatchConstructorArguments() {
        assertEquals("Initial x", 30, floatingCharacter.getX(), 0);
        assertEquals("Initial y", 30, floatingCharacter.getY(), 0);
        assertEquals("Offset x", 0, floatingCharacter.getOffsetX(), 0.0);
        assertEquals("Offset y", 0, floatingCharacter.getOffsetY(), 0.0);
        assertEquals("Left key", KeyCode.A, floatingCharacter.getLeftKey());
        assertEquals("Right key", KeyCode.D, floatingCharacter.getRightKey());
        assertEquals("Up key", KeyCode.W, floatingCharacter.getUpKey());
    }

    @Test
    public void characterShouldMoveToTheLeftAfterTheLeftKeyIsPressed() throws IllegalAccessException, InvocationTargetException,
     NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.A);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveLeft = characterUnderTest.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        assertTrue("Controller: Left key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model: Character moving left state is set", isMoveLeft.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving left", characterUnderTest.getX() < startX);
    }
    @Test
    public void characterShouldMoveToTheRightAfterTheRightKeyIsPressed() throws IllegalAccessException, InvocationTargetException, 
    NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startX = characterUnderTest.getX();
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        Field isMoveRight = characterUnderTest.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);
        assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertTrue("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTest));
        assertTrue("View: Character is moving right", characterUnderTest.getX() > startX);
    }

    @Test
    public void characterShouldJumpWhenThereIsGround() throws IllegalAccessException, InvocationTargetException,
    NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(3);
        int startY;

        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        isJumping.setAccessible(true);
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling");
        isFalling.setAccessible(true);
        Field canJump = characterUnderTest.getClass().getDeclaredField("canJump");
        canJump.setAccessible(true);

        startY = characterUnderTest.getY();
        characterUnderTest.checkReachFloor();
        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);

        assertTrue("Controller: Jump key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertFalse("Model: Character can not jump", canJump.getBoolean(characterUnderTest));
        assertFalse("Model: Character is not falling", isFalling.getBoolean(characterUnderTest));
        assertTrue("Model: Character is jumping", isJumping.getBoolean(characterUnderTest));
        assertTrue("View: Character is in the air", characterUnderTest.getY() < startY);
    
    }

    @Test
    public void characterShouldNotJumpWhenThereIsNoGround() throws IllegalAccessException, InvocationTargetException,
    NoSuchFieldException {
        Character characterUnderTest = characterListUnderTest.get(0);
        int startY = characterUnderTest.getY();

        Field isJumping = characterUnderTest.getClass().getDeclaredField("isJumping");
        isJumping.setAccessible(true);
        Field isFalling = characterUnderTest.getClass().getDeclaredField("isFalling");
        isFalling.setAccessible(true);
        Field canJump = characterUnderTest.getClass().getDeclaredField("canJump");
        canJump.setAccessible(true);

        platformUnderTest.getKeys().add(KeyCode.W);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);

        assertTrue("Controller: Jump key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.W));
        assertFalse("Model: Character can not jump", canJump.getBoolean(characterUnderTest));
        assertTrue("Model: Character is falling", isFalling.getBoolean(characterUnderTest));
        assertFalse("Model: Character is not jumping", isJumping.getBoolean(characterUnderTest));
        assertTrue("View: Character is in the air", characterUnderTest.getY() > startY);
        
    }

    @Test
    public void characterWallCollision() throws IllegalAccessException, InvocationTargetException,
    NoSuchFieldException {
        Character characterUnderTestLeftBorder = characterListUnderTest.get(3);
        Character characterUnderTestRightBorder = characterListUnderTest.get(5);

        int startXLeft = characterUnderTestLeftBorder.getX();
        int startXRight = characterUnderTestRightBorder.getX();

        platformUnderTest.getKeys().add(KeyCode.A);
        platformUnderTest.getKeys().add(KeyCode.RIGHT);
        
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterUnderTestLeftBorder.checkReachGameWall();
        characterUnderTestRightBorder.checkReachGameWall();

        Field isMoveLeft = characterUnderTestLeftBorder.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        Field isMoveRight = characterUnderTestRightBorder.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);

        assertTrue("Controller: Left key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.A));
        assertTrue("Model: Character moving left state is set", isMoveLeft.getBoolean(characterUnderTestLeftBorder));
        assertEquals("View: Character is in the same position", characterUnderTestLeftBorder.getX(), startXLeft);
                        
        assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.RIGHT));
        assertTrue("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTestRightBorder));
        assertEquals("View: Character is in the same position", characterUnderTestRightBorder.getX(), startXRight);        
        
    }

    @Test
    public void characterOtherCollision() throws IllegalAccessException, InvocationTargetException,
    NoSuchFieldException {
        Character characterUnderTestLeft = characterListUnderTest.get(3);
        Character characterUnderTestRight = characterListUnderTest.get(4);
        int startXLeft = characterUnderTestLeft.getX();
        
        platformUnderTest.getKeys().add(KeyCode.D);
        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        characterUnderTestLeft.collided(characterUnderTestRight);

        Field isMoveRight = characterUnderTestLeft.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);

        assertTrue("Controller: Right key pressing is acknowledged",platformUnderTest.getKeys().isPressed(KeyCode.D));
        assertFalse("Model: Character moving right state is set", isMoveRight.getBoolean(characterUnderTestLeft));
        assertEquals("View: Character is in the same position", startXLeft, characterUnderTestLeft.getX());

    }

    @Test
    public void characterStomp() throws IllegalAccessException, InvocationTargetException,
    NoSuchFieldException {
        Character firstCharacter = characterListUnderTest.get(0);
        Character secondCharacter = characterListUnderTest.get(2);
        int startXFirst = firstCharacter.getX();
        int startYFirst = firstCharacter.getY();

        Field isFalling = firstCharacter.getClass().getDeclaredField("isFalling");
        isFalling.setAccessible(true);
        Field isJumping = firstCharacter.getClass().getDeclaredField("isJumping");
        isJumping.setAccessible(true);
        Field canJump = firstCharacter.getClass().getDeclaredField("canJump");
        canJump.setAccessible(true);
        Field isMoveLeft = firstCharacter.getClass().getDeclaredField("isMoveLeft");
        isMoveLeft.setAccessible(true);
        Field isMoveRight = firstCharacter.getClass().getDeclaredField("isMoveRight");
        isMoveRight.setAccessible(true);        
        Field score = secondCharacter.getClass().getDeclaredField("score");
        score.setAccessible(true);

        updateMethod.invoke(gameLoopUnderTest,characterListUnderTest);
        redrawMethod.invoke(drawingLoopUnderTest,characterListUnderTest);
        secondCharacter.collided(firstCharacter);

        assertTrue("Stomping character bounce up",Platform.GROUND - Character.CHARACTER_HEIGHT - 5 <= secondCharacter.getY());
        assertTrue("Stomping character should have a score of 1", score.getInt(secondCharacter) == 1);
        assertTrue("Stomped character respawn at startX", startXFirst == firstCharacter.getX());
        assertTrue("Stomped character respawn at startY", startYFirst == firstCharacter.getY());
        assertTrue("Stomped character should have the same height", Character.CHARACTER_HEIGHT == firstCharacter.getImageView().getFitHeight());
        assertTrue("Stomped character should have the same width", Character.CHARACTER_WIDTH == firstCharacter.getImageView().getFitWidth());
        assertTrue("Stomped character.isFalling is true", isFalling.getBoolean(firstCharacter));
        assertFalse("Stomped character.isJumping is false", isJumping.getBoolean(firstCharacter));
        assertFalse("Stomped character.canJump is false", canJump.getBoolean(firstCharacter));
        assertFalse("Stomped character.isMoveLeft is false", isMoveLeft.getBoolean(firstCharacter));
        assertFalse("Stomped character.isMoveRight is false", isMoveRight.getBoolean(firstCharacter));
    }
}
