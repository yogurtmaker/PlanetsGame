package dodgeball;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import java.util.LinkedList;

public class Game extends AbstractAppState implements ActionListener {

    Main main;
    AppStateManager asm;
    LinkedList<BallControl> ballAvailable;
    LinkedList<EnemyControl> waitingForBall;
    BitmapText scoreText, waitText, pauseText;
    PlayerControl playerControl;
    Player player;
    Arena arena;
    
    float gameTime;
    float waitTime;
    boolean waitTextVisible = false;
    private final int WAIT = 0;
    private final int RUN = 1;
    private final int PAUSE = 2;
    private int state, stateMemory;
    // game parameters
    private final int INITIALWAITTIME = 3; // seconds
    private final int MAXHITS = 10;
    private float cameraXOffset;
    protected boolean isDemo;
    private final int NUMBALLS = 5; // number of balls in game

    protected Game(boolean iDemoMode) {
        isDemo = iDemoMode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        // create arena, player etc.
        waitingForBall = new LinkedList<EnemyControl>();
        ballAvailable = new LinkedList<BallControl>();
        arena = new Arena(this);
        main.getRootNode().attachChild(arena); 
                
        // crate enemies and attach controls
        // the vector is the homebase
        Vector3f[] bases = {
            new Vector3f(0, 0, -1),
            new Vector3f(1, 0, 0),
            new Vector3f(0, 0, 1),
            new Vector3f(-1, 0, 0)
        };
        for (int i = 0; i < 4; i++) {
            Enemy e = new Enemy();
            e.addControl(new EnemyControl(bases[i], i * 90, this));
            main.getRootNode().attachChild(e);
        }

        // create balls and attach controls
        for (int i = 0; i < NUMBALLS; i++) {
            Ball b = new Ball();
            BallControl bc = new BallControl(this);
            b.addControl(bc);
            ballAvailable.addFirst(bc);
            main.getRootNode().attachChild(b);
        }

        // create player and attach control
        player = new Player();
        playerControl = new PlayerControl(this);
        player.addControl(playerControl);
        main.getRootNode().attachChild(player);

        // init game states
        // gameTime is the internal game status, 
        // used by the state machine of this class
        gameTime = 0;
        state = WAIT;
        waitTime = INITIALWAITTIME; //seconds

        // Texts
        BitmapFont bmf = main.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        scoreText = new BitmapText(bmf);
        scoreText.setSize(bmf.getCharSet().getRenderedSize() * 2);
        scoreText.setColor(ColorRGBA.Black);
        scoreText.setText("");
        scoreText.setLocalTranslation(20, 20, 0f);
        main.getGuiNode().attachChild(scoreText);
        waitText = new BitmapText(bmf);
        waitText.setSize(bmf.getCharSet().getRenderedSize() * 10);
        waitText.setColor(ColorRGBA.White);
        waitText.setText("");
        AppSettings s = main.getSettings();
        float lineY = s.getHeight() / 2;
        float lineX = (s.getWidth() - waitText.getLineWidth()) / 2;
        waitText.setLocalTranslation(lineX, lineY, 0f);
        pauseText = new BitmapText(bmf);
        pauseText.setSize(bmf.getCharSet().getRenderedSize() * 10);
        pauseText.setColor(ColorRGBA.White);
        pauseText.setText("PAUSED");
        lineY = s.getHeight() / 2;
        lineX = (s.getWidth() - pauseText.getLineWidth()) / 2;
        pauseText.setLocalTranslation(lineX, lineY, 0f);

        // keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(this, "Pause", "Quit");

        // skybox, cam       
        initViews();
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            // trigger pause and quit
            if (name.equals("Pause")) {
                if (state == PAUSE) {
                    state = stateMemory;
                    main.getGuiNode().detachChild(pauseText);
                } else {
                    stateMemory = state;
                    state = PAUSE;
                    main.getGuiNode().attachChild(pauseText);
                }
            }
            if (name.equals("Quit")) {
                // state transition to start screen
                StartScreen s = new StartScreen();
                asm.detach(this);
                asm.attach(s);
            }
        }
    }

    private void initViews() {
        main.getFlyByCamera().setEnabled(false);
        main.getCamera().setLocation(new Vector3f(3, 15, 20));
        main.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        Spatial sky = SkyFactory.createSky(
                main.getAssetManager(), "Textures/texture1.png", true);
        main.getRootNode().attachChild(sky);
    }

    // Game State Machine
    @Override
    public void update(float tpf) {
        // camera movement is independent of game state, therefore it's handled
        // outside of the state machine cases.
        // camera does not kick in in demo mode.
        if (!isDemo) {
            Vector3f camLoc = main.getCamera().getLocation();
            Vector3f playerLoc = playerControl.getSpatial().getLocalTranslation();
            camLoc.x = playerLoc.x / arena.edgeLength * 5 + 3 + FastMath.sin(cameraXOffset);
            cameraXOffset += tpf;
            main.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        }

        switch (state) {
            case WAIT:
                if (isDemo) {
                    state = RUN;  // no wait in demo
                    break;
                }
                if (!waitTextVisible) {
                    waitTextVisible = true;
                    main.getGuiNode().attachChild(waitText);
                }
                waitTime -= tpf;
                if (waitTime <= 0f) {
                    state = RUN;
                    if (waitTextVisible) {
                        waitTextVisible = false;
                        main.getGuiNode().detachChild(waitText);
                    }
                } else {
                    waitText.setText("" + ((int) waitTime + 1));
                }
                break;
            case RUN:
                stateRun(tpf);
                if (!isDemo && playerControl.getNumHits() == MAXHITS) {
                    // game over: maxhits reached
                    endGame();
                }
                break;
            case PAUSE:
                // do nothing
                break;
        }
    }

    // Transition to EndScreen!
    private void endGame() {
        EndScreen end = new EndScreen();
        double[] dummy = {playerControl.getNumHits(), gameTime};
        end.setStats(dummy);
        asm.detach(this);
        asm.attach(end);
    }

    private void stateRun(float tpf) {
        // hand out balls to waiting enemies
        while (!waitingForBall.isEmpty() && !ballAvailable.isEmpty()) {
            BallControl bc = ballAvailable.removeLast();
            EnemyControl ec = waitingForBall.removeLast();
            if (ec != null) {
                ec.receiveBall(bc);
            }
        }

        // enemy, player and ball update are performed by the controls!
        // they are not to find here!

        // time etc
        gameTime += tpf;

        // update text
        if (!isDemo) {
            String t = String.format("Hits: %d \tTime: %3.1f", playerControl.getNumHits(), gameTime);
            scoreText.setText(t);
        }
    }
}
