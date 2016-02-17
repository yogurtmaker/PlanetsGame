package dodgeball;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;

public class EnemyControl extends AbstractControl {

    private Vector3f homeBase, posDirection;
    private Game game;
    private float speed = 1;
    private float target;
    private float direction;
    private float position;
    private boolean atTarget = false;
    private int state;
    private final int SETTARGET = 1;
    private final int MOVE = 2;
    private final int SHOOT = 3;
    private final int WAITFORBALL = 4;
    private BallControl ball;
    private float tpfSum;
    Vector3f boxsize = new Vector3f(0.5f, 1f, 0.6f);
    Quaternion rotation;

    public EnemyControl(Vector3f iStart, int rot, Game iGame) {
        game = iGame;
        homeBase = iStart.mult(game.arena.edgeLength / 2 + boxsize.z);
        switch (rot) {
            case 0:
                posDirection = Vector3f.UNIT_X;
                break;
            case 90:
                posDirection = Vector3f.UNIT_Z;
                break;
            case 180:
                posDirection = new Vector3f(-1, 0, 0);
                break;
            case 270:
                posDirection = new Vector3f(0, 0, -1);
                break;
        }
        rotation = new Quaternion();
        rotation.fromAngleAxis((float) (-rot) * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);

        // start in waitstate 
        setWaitState();
    }

    // move enemy
    // handles position variables and translation of the geometry
    public void move(float tpf) {
        if (!atTarget) {
            if (direction < 0 && position <= target) {
                atTarget = true;
                position = target;
                return;
            }
            if (direction > 0 && position >= target) {
                atTarget = true;
                position = target;
                return;
            }
            if (direction < 0) {
                position -= tpf * speed;
            } else {
                position += tpf * speed;
            }
        }
    }

    // set state to WAITFORBALL and add Enemy to the list
    // waiting for ball.
    private void setWaitState() {
        state = WAITFORBALL;
        game.waitingForBall.addFirst(this);
    }

    // called by Game class
    public void receiveBall(BallControl iBall) {
        ball = iBall;
        ball.assignEnemy((Enemy)spatial);
    }
    
    // -------------------------------------------------------------------------
    // STATE MACHINE
    protected void controlUpdate(float tpf) {
        switch (state) {
            case SETTARGET:
                // reached from receiveBall()
                // sets a target position ([-1:1]) and changes
                // state to MOVE
                target = FastMath.rand.nextFloat() * 2f - 1f;
                direction = target >= position ? 1 : -1;
                atTarget = false;
                state = MOVE;
            case MOVE:
                // reached directly form SETTARGFET
                move(tpf);
                if (atTarget) {
                    state = SHOOT;
                } else {
                    break;
                }
            case SHOOT:
                // reached from state MOVE when enemy is at target
                // position
                Vector3f thr = new Vector3f(-posDirection.z, 0, posDirection.x);
                Matrix3f ry = new Matrix3f();
                float angle = FastMath.rand.nextFloat() * 0.6f - 0.3f;
                ry.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
                ry.multLocal(thr);

                Vector3f pos = spatial.getLocalTranslation();
                pos.y = 0.5f;
                ball.throwBall(pos, thr, 15.0f);
                ball = null; // important! is checked in WAITFORBALL!
                setWaitState();

                break;
            case WAITFORBALL:
                // wait until a ball is available. Then switch to SETARGET
                if (ball != null) {
                    state = SETTARGET;
                }
                break;
        }
        
        // hand;le spatial transformation
        // wobbling enemies: scale
        tpfSum += tpf;
        float scale = (FastMath.sin(tpfSum * 15) + 1f) * 0.25f + 1f;
        spatial.setLocalScale(scale * boxsize.x, boxsize.y * scale, boxsize.z);

        // set rotation
        spatial.setLocalRotation(rotation);

        // translate position to geometry location
        scale = game.arena.edgeLength / 2f * position;
        Vector3f loc = posDirection.mult(scale).add(homeBase);
        spatial.setLocalTranslation(loc);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
