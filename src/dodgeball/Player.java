package dodgeball;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;

public class Player extends Node {

    protected float radius = 0.5f;
    float height = 1.0f;
    Node playerGeometries;
    Geometry geomHead;
    Geometry geomBody;

    public Player() {
        Cylinder body = new Cylinder(10, 10, radius, height, true);
        geomBody = new Geometry("body", body);
        geomBody.rotate(90f * FastMath.DEG_TO_RAD, 0, 0);
        geomBody.setLocalTranslation(0f, height / 2f, 0f);
        geomBody.setShadowMode(RenderQueue.ShadowMode.Cast);

        Sphere head = new Sphere(10, 10, radius);
        geomHead = new Geometry("head", head);
        geomHead.rotate(90f * FastMath.DEG_TO_RAD, 0, 0);
        geomHead.setLocalTranslation(0f, height + radius, 0f);
        geomHead.setShadowMode(RenderQueue.ShadowMode.Cast);

        setPlayerMaterial(Main.matOrange);
        attachChild(geomBody);
        attachChild(geomHead);
    }

    public void setPlayerMaterial(Material m) {
        geomHead.setMaterial(m);
        geomBody.setMaterial(m);
    }
}
