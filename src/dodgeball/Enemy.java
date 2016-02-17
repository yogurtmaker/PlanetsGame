package dodgeball;

import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Enemy extends Node{
    
    protected final Geometry geom;

    public Enemy() {
        Sphere mesh = new Sphere(30, 30, 1.0f);
        geom = new Geometry("Enemy", mesh);
        geom.setMaterial(Main.matBlue);
        geom.setShadowMode(RenderQueue.ShadowMode.Cast);
        attachChild(geom);
    }
}
