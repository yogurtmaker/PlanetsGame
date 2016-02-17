package dodgeball;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;

public class BallControl extends AbstractControl {

    private final Game game;
    private Vector3f direction;
    private Vector3f position;
    private float speed = 20f;
    private int state = 0;
    private Enemy enemy;
    private final int IDLE = 0;
    private final int ASSIGNED = 1;
    private final int FLYING = 2;

    public BallControl(Game game) {
        this.game = game;
        state = IDLE;
    }

    public boolean isAvailable() {
        return (state == IDLE);
    }

    // called by Enemy
    public void assignEnemy(Enemy e) {
        enemy = e;
        state = ASSIGNED;
    }

    // called by EnemyControl
    // called by Enemy
    public void throwBall(Vector3f p, Vector3f d, float s) {
        position = p.clone();
        direction = d;
        speed = s;
        state = FLYING;
    }

    private boolean checkCollision() {
        Vector3f pos = game.player.getLocalTranslation();
        float diffX = pos.x - position.x;
        float diffZ = pos.z - position.z;
        float diff = FastMath.sqrt(diffX * diffX + diffZ * diffZ);
        return (diff <= ((Ball) spatial).radius + game.player.radius);
    }

    // STATE MACHINE
    public void controlUpdate(float tpf) {
        switch (state) {
            case IDLE:
                // state reached either from 
                // FLYING or (Constructor)
                spatial.setLocalTranslation(0, -3f, 0);
                break;
            case ASSIGNED:
                // state reached through assignEnemy()
                Vector3f enp = enemy.getLocalTranslation();
                spatial.setLocalTranslation(enp);
                spatial.move(0, 2.0f, 0);
                break;
            case FLYING:
                // state reached through throwBall()
                position = position.add(direction.mult(speed*tpf));
                spatial.setLocalTranslation(position);
                boolean out = game.arena.checkBoundaries(position, -1.0f);
                boolean collision = checkCollision();
                if (out || collision){
                    state = IDLE;
                    game.ballAvailable.addFirst(this);
                }
                if (collision){
                    game.playerControl.markPlayerBeingHit();
                }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
