package thegame.gameobjects;

import com.javarush.engine.cell.Game;
import thegame.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemyFleet {
    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length+1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    private void createShips() {
        ships = new ArrayList<EnemyShip>();
        for(int i = 0; i < COLUMNS_COUNT; i++) {
            for(int j = 0; j < ROWS_COUNT; j++) {
                ships.add(new EnemyShip(i*STEP, j*STEP+12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    public EnemyFleet() {
        createShips();
    }

    public  void draw(Game game) {
        for(EnemyShip ship : ships) {
            ship.draw(game);
        }
    }

    private double getLeftBorder() {
        double minX = ships.get(0).x;
        for(EnemyShip ship : ships) {
            if(minX > ship.x) {
                minX = ship.x;
            }
        }
        return minX;
    }

    private double getRightBorder() {
        double maxX = 0.0;
        for(EnemyShip ship : ships) {
            if(ship.x + ship.width > maxX) {
                maxX = ship.x + ship.width;
            }
        }
        return maxX;
    }

    private double getSpeed() {
        return Math.min(2.0, (3.0 / ships.size()));
    }

    public void move() {
        if(ships.size() > 0) {
            if (direction == Direction.LEFT && getLeftBorder() < 0) {
                direction = Direction.RIGHT;
                for (EnemyShip ship : ships) {
                    ship.move(Direction.DOWN, getSpeed());
                }
            }
            if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
                direction = Direction.LEFT;
                for (EnemyShip ship : ships) {
                    ship.move(Direction.DOWN, getSpeed());
                }
            }
            for (EnemyShip ship : ships) {
                ship.move(direction, getSpeed());
            }
        }
    }

    public Bullet fire(Game game) {
        if(ships.size() == 0) {
            return null;
        }
        if(game.getRandomNumber(100/SpaceInvadersGame.COMPLEXITY) > 0) {
            return null;
        }
        return ships.get(game.getRandomNumber(ships.size())).fire();
    }

    public int verifyHit(List<Bullet> bullets) {
        if(bullets.size() == 0) {
            return 0;
        } else {
            int gain = 0;
            for (EnemyShip ship : ships) {
                for (Bullet bullet : bullets) {
                    if (ship.isCollision(bullet) && ship.isAlive && bullet.isAlive) {
                        ship.kill();
                        bullet.kill();
                        gain = gain + ship.score;
                    }
                }
            }
            return gain;
        }
    }

    public void deleteHiddenShips() {
        Iterator iterator = ships.iterator();
        while(iterator.hasNext()) {
            Ship testShip =(Ship) iterator.next();
            if(!testShip.isVisible())
                iterator.remove();
        }
    }

    public double getBottomBorder() {
        if(ships.size() > 0) {
            double maxY = ships.get(0).y + ships.get(0).height;
            for (Ship ship : ships) {
                if (ship.y + ship.height > maxY) {
                    maxY = ship.y + ship.height;
                }
            }
            return maxY;
        }
        return 0;
    }

    public int getShipsCount() {
        return ships.size();
    }

}

