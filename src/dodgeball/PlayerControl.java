package dodgeball;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class PlayerControl extends AbstractControl implements AnalogListener {

    float speed = 8;
    Game game;
    boolean wasHit = false;
    int state;
    private final int MOVING = 1;
    private final int HIT = 2;
    private final float HITTIME = 2.0f;
    private float hitTimeCounter;
    private int numHits = 0;

    public PlayerControl(Game game) {
        this.game = game;

        // Keys
        InputManager inputManager = game.main.getInputManager();
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, "Left", "Right", "Forward", "Backward");
        state = MOVING;
    }

    protected void markPlayerBeingHit() {
        wasHit = true;
        numHits++;
    }

    protected void controlUpdate(float tpf) {
        switch (state) {
            case MOVING:
                // transition to HIT
                if (wasHit) {
                    hitTimeCounter = HITTIME;
                    wasHit = false;
                    state = HIT;
                    // color change
                    ((Player) spatial).setMaterial(Main.matBlue);
                }
                break;
            case HIT:
                // re-init! a player can be hit while he is still down.
                if (wasHit) {
                    hitTimeCounter = HITTIME;
                    wasHit = false;
                }
                hitTimeCounter -= tpf;

                // hit-animation
                // normalCount goes from 0 to 1 in HITTIME seconds
                float normalCount = 1f - hitTimeCounter / HITTIME;
                float scale = FastMath.cos(normalCount * 150f) * 0.1f; // -0.1 .. 0.1
                scale += 1f;
                scale *= normalCount * 0.8f + 0.2f;
                spatial.setLocalScale(scale); // wobble and grow

                // Transition to moving
                if (hitTimeCounter <= 0.0f) {
                    ((Player) spatial).setMaterial(Main.matOrange);
                    state = MOVING;
                    spatial.setLocalScale(1f);
                }
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        if (!game.isDemo && state == MOVING) {
            Vector3f position = spatial.getLocalTranslation();
            if (name.equals("Left")) {
                position.x -= tpf * speed;
            } else if (name.equals("Right")) {
                position.x += tpf * speed;
            } else if (name.equals("Forward")) {
                position.z -= tpf * speed;
            } else if (name.equals("Backward")) {
                position.z += tpf * speed;
            }

            spatial.setLocalTranslation(position);
            game.arena.checkBoundaries(position, ((Player) spatial).radius);
        }
    }

    public int getNumHits() {
        return numHits;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
