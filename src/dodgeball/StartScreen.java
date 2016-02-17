package dodgeball;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class StartScreen extends AbstractAppState implements ActionListener {

    BitmapText text;
    private Node cameraTarget;
    Main main;
    FilterPostProcessor fpp;
    AppStateManager asm;
    boolean realGameStarted = false;

    // State Transitions are coded here!
    public void onAction(String name, boolean keyDown, float tpf) {
        if (keyDown) {
            if (name.equals("Start") && !realGameStarted) {
              Game game = new Game(false);
                 asm.detach(this);   
                 asm.attach(game);
                realGameStarted = true; // this is needed to avoid triggering multiple games
                                        // if someone pressed space multiple times      
            }
            if (name.equals("Quit")) {
                // this is brutal.
                System.exit(0);
            }
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        Main.clearJMonkey(main);

        // Text
        //main.setGuiFont(main.getAssetManager().loadFont("Interface/Fonts/Console.fnt"));
        BitmapFont bmf = app.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        text = new BitmapText(bmf);
        text.setSize(bmf.getCharSet().getRenderedSize() * 10);
        text.setColor(ColorRGBA.Red);
        text.setText("DODGEBALL");
        main.getGuiNode().attachChild(text);

        // set camera location
        main.getFlyByCamera().setEnabled(false);
        cameraTarget = new Node();
        CameraNode camNode = new CameraNode("Camera Node", main.getCamera());
        camNode.setLocalTranslation(new Vector3f(0f, 6f, 15f));
        camNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraTarget.attachChild(camNode);
        main.getRootNode().attachChild(cameraTarget);

        // Keys
        InputManager inputManager = main.getInputManager();
        inputManager.clearMappings();
        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(this, "Start", "Quit");

        // background: run a game in demo mode in the background!
        Game demoGame = new Game(true); // true => demo mode
        stateManager.attach(demoGame);

        // and set blurring
        initPostProcessing();
    }

    private void initPostProcessing() {
        fpp = new FilterPostProcessor(main.getAssetManager());
        BloomFilter bloom = new BloomFilter();
        bloom.setBlurScale(3.0f);
        bloom.setBloomIntensity(10.0f);
        fpp.addFilter(bloom);
        main.getViewPort().addProcessor(fpp);
        Spatial sky = SkyFactory.createSky(
                main.getAssetManager(), "Textures/texture1.png", true);
        main.getRootNode().attachChild(sky);
    }

    @Override
    public void cleanup() {
        main.getViewPort().removeProcessor(fpp);
        Main.clearJMonkey(main);
    }

    @Override
    public void update(float tpf) {
        AppSettings s = main.getSettings();
        float lineY = s.getHeight() / 2 + FastMath.rand.nextFloat() * 10 - 5;
        float lineX = (s.getWidth() - text.getLineWidth()) / 2 + FastMath.rand.nextFloat() * 10 - 5;
        text.setLocalTranslation(lineX, lineY, 0f);

        //
        cameraTarget.rotate(0, tpf, 0);
    }
}
