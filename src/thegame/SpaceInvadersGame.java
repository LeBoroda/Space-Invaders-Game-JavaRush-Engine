package thegame;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;
import com.javarush.engine.cell.Key;
import thegame.gameobjects.Bullet;
import thegame.gameobjects.EnemyFleet;
import thegame.gameobjects.PlayerShip;
import thegame.gameobjects.Star;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceInvadersGame<Star> extends Game {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int COMPLEXITY = 5;
    private static final int PLAYER_BULLETS_MAX = 1;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    private List<Bullet> enemyBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private List<Bullet> playerBullets;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<Bullet>();
        playerShip = new PlayerShip();
        isGameStopped = false;
        animationsCount = 0;
        playerBullets = new ArrayList<Bullet>();
        score = 0;
        drawScene();
        setTurnTimer(40);
    }

    private void drawScene() {
        drawField();
        enemyFleet.draw(this);
        for(Bullet bullet : enemyBullets) {
            bullet.draw(this);
        }
        playerShip.draw(this);
        for(Bullet bullet : playerBullets) {
            bullet.draw(this);
        }
    }

    private void drawField() {
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                setCellValueEx(i, j, Color.BLACK, "");
            }
        }
        for(Star star : stars) {
            star.draw(this);
        }
    }

    private void createStars() {
        stars = new ArrayList<Star>();
        for (int i = 0; i < 8; i++) {
            Double randomNumberX =(double) getRandomNumber(0,WIDTH);
            Double randomNumberY =(double) getRandomNumber(0,HEIGHT);
            stars.add(new Star(randomNumberX, randomNumberY));
        }

    }

    @Override
    public void onTurn(int step) {
        moveSpaceObjects();
        check();
        Bullet tempBullet;
        if((tempBullet = enemyFleet.fire(this)) != null) {
            enemyBullets.add(tempBullet);
        }
        setScore(score);
        drawScene();
    }

    private void moveSpaceObjects() {
        enemyFleet.move();
        for(Bullet bullet : enemyBullets) {
            bullet.move();
        }
        playerShip.move();
        for(Bullet bullet : playerBullets) {
            bullet.move();
        }
    }

    private void removeDeadBullets() {
        Iterator iterator1 = enemyBullets.iterator();
        while (iterator1.hasNext()) {
            Bullet checkBullet = (Bullet) iterator1.next();
            if(checkBullet.y >= HEIGHT-1 || !checkBullet.isAlive) {
                iterator1.remove();
            }
        }
        Iterator iterator2 = playerBullets.iterator();
        while (iterator2.hasNext()) {
            Bullet checkBullet = (Bullet) iterator2.next();
            if(checkBullet.y + checkBullet.height < 0 || !checkBullet.isAlive) {
                iterator2.remove();
            }
        }
    }

    private void check() {
        playerShip.verifyHit(enemyBullets);
        score += enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        removeDeadBullets();
        if(!playerShip.isAlive) {
            stopGameWithDelay();
        }
        if(enemyFleet.getBottomBorder() >= playerShip.y) {
            playerShip.kill();
        }
        if(enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }
    }

    private void stopGame(boolean isWin) {
        isGameStopped = true;
        stopTurnTimer();
        if(isWin) {
            showMessageDialog(Color.BLACK, "Нашествие отражено!", Color.GREEN, 30);
        } else {
            showMessageDialog(Color.BLACK, "Пластмассовый мир победил!", Color.RED, 30);
        }
    }

    private void stopGameWithDelay() {
        animationsCount++;
        if(animationsCount >=10)
            stopGame(playerShip.isAlive);
    }

    @Override
    public void onKeyPress(Key key) {
        if(isGameStopped && key == Key.SPACE)
            createGame();
        if(key == Key.LEFT)
            playerShip.setDirection(Direction.LEFT);
        if(key == Key.RIGHT)
            playerShip.setDirection(Direction.RIGHT);
        if(key == Key.SPACE) {
            if(playerShip.fire() != null && playerBullets.size() < PLAYER_BULLETS_MAX) {
                playerBullets.add(playerShip.fire());
            }
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if(playerShip.getDirection() == Direction.LEFT && key == Key.LEFT) {
            playerShip.setDirection(Direction.UP);
        }
        if(playerShip.getDirection() == Direction.RIGHT && key == Key.RIGHT) {
            playerShip.setDirection(Direction.UP);
        }
    }

    @Override
    public void setCellValueEx(int x, int y, Color cellColor, String value) {
        if(x > 0 && x < WIDTH && y > 0 && y < HEIGHT){
            super.setCellValueEx(x, y, cellColor, value);
        }
    }
}
