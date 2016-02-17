package dodgeball;

import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Ball extends Node{
    protected final float radius = 0.2f;
    private final Geometry geom;  

    public Ball() {
        Sphere mesh = new Sphere(10, 10, radius);
        geom = new Geometry("Ball", mesh);
        geom.setMaterial(Main.matRed);
        geom.setLocalTranslation(0f, -radius*2f, 0f);
        geom.setShadowMode(RenderQueue.ShadowMode.Cast);
        attachChild(geom);
    }
}
