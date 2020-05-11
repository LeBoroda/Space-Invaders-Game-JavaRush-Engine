package thegame.gameobjects;

import thegame.*;
import thegame.ShapeMatrix;
import thegame.SpaceInvadersGame;

import java.util.List;

public class PlayerShip extends Ship {

    private Direction direction = Direction.UP;

    public void setDirection(Direction newDirection) {
        if(newDirection != Direction.DOWN)
            direction = newDirection;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public PlayerShip() {
        super(SpaceInvadersGame.WIDTH / 2.0, SpaceInvadersGame.HEIGHT - ShapeMatrix.PLAYER.length - 1);
        setStaticView(ShapeMatrix.PLAYER);

    }

    public void verifyHit(List<Bullet> bullets) {
        if(bullets.size() > 0) {
            for(Bullet bullet : bullets) {
                if(this.isAlive && bullet.isAlive) {
                    if( isCollision(bullet)) {
                        this.kill();
                        bullet.kill();
                    }
                }
            }
        }
    }

    @Override
    public void kill() {
        if(isAlive) {
            isAlive = false;
            setAnimatedView(false, ShapeMatrix.KILL_PLAYER_ANIMATION_FIRST,
                    ShapeMatrix.KILL_PLAYER_ANIMATION_SECOND,
                    ShapeMatrix.KILL_PLAYER_ANIMATION_THIRD,
                    ShapeMatrix.DEAD_PLAYER);
        }
    }

    public void move() {
        if(isAlive) {
            if(direction == Direction.LEFT)
                x--;
            else if(direction == Direction.RIGHT)
                x++;
            if(x < 0)
                x = 0;
            if(x+width > SpaceInvadersGame.WIDTH)
                x = SpaceInvadersGame.WIDTH - width;
        }
    }

    @Override
    public Bullet fire() {
        if(isAlive) {
            return new Bullet(x + 2, y - ShapeMatrix.BULLET.length, Direction.UP);
        } else {
            return null;
        }
    }

    public void win() {
        setStaticView(ShapeMatrix.WIN_PLAYER);
    }
}

