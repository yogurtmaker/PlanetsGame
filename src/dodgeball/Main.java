package dodgeball;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {

    static Material matRed, matGreen, matBlue, matOrange, matTransparent;
    Game game;
       StartScreen s ;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        //settings.setFullscreen(true);
        settings.setVSync(true);
        Main app = new Main();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    public AppSettings getSettings() {
        return (settings);
    }

    @Override
    public void simpleInitApp() {
        // some inits
        initMaterials();
        initLights();
        initCam();
        initGUI();

        // This starts the game!
    s  = new StartScreen();
        stateManager.attach(s);
    }

    protected static void clearJMonkey(Main m) {
        m.guiNode.detachAllChildren();
        m.rootNode.detachAllChildren();
        m.inputManager.clearMappings();
    }

    @Override
    public void simpleUpdate(float tpf) {
        // this is empty, since the game logic is handled by AppStates
    }

    // -------------------------------------------------------------------------
    public void initMaterials() {
        matRed = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matRed.setColor("Color", ColorRGBA.Orange);
        matRed = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matRed.setBoolean("UseMaterialColors", true);
        matRed.setColor("Ambient", new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        matRed.setColor("Diffuse", ColorRGBA.Red);
        matRed.setTexture("DiffuseMap", assetManager.loadTexture("Textures/texture1.png"));

        matGreen = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matGreen.setBoolean("UseMaterialColors", true);
        matGreen.setColor("Ambient", new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        matGreen.setColor("Diffuse", ColorRGBA.Green);
        matGreen.setColor("Specular", ColorRGBA.Orange);
        matGreen.setFloat("Shininess", 2f); // shininess from 1-128
        matGreen.setTexture("DiffuseMap", assetManager.loadTexture("Textures/texture1.png"));

        matBlue = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        matBlue.setColor("Color", ColorRGBA.Orange);
        matBlue = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matBlue.setBoolean("UseMaterialColors", true);
        matBlue.setColor("Ambient", new ColorRGBA(0.6f, 0.6f, 0.6f, 1.0f));
        matBlue.setColor("Diffuse", ColorRGBA.Blue);
        matBlue.setTexture("DiffuseMap", assetManager.loadTexture("Textures/texture1.png"));

        matOrange = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matOrange.setBoolean("UseMaterialColors", true);
        matOrange.setColor("Ambient", new ColorRGBA(0.3f, 0.3f, 0.3f, 1.0f));
        matOrange.setColor("Diffuse", ColorRGBA.Orange);
        matOrange.setColor("Specular", ColorRGBA.Orange);
        matOrange.setFloat("Shininess", 2f); // shininess from 1-128
        matOrange.setTexture("DiffuseMap", assetManager.loadTexture("Textures/texture1.png"));
    }

    // -------------------------------------------------------------------------
    public void initLights() {
        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);
        /**
         * A white, directional light source
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.3f, -0.4f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // SHADOW
        // the second parameter is the resolution. Experiment with it! (Must be a power of 2)
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
    }

    private void initGUI() {
        setDisplayStatView(false);
        setDisplayFps(true);
    }

    private void initCam() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(3, 15, 20));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    }
}
