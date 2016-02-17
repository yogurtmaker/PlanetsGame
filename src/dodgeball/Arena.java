/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dodgeball;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class Arena extends Node{

    Geometry geom;
    float edgeLength = 20.0f;

    public Arena(Game game) {
        Box mesh = new Box(edgeLength / 2f, 0.4f, edgeLength / 2f);
        geom = new Geometry("Arena", mesh);
        geom.setMaterial(Main.matGreen);
        geom.setLocalTranslation(0f, -0.4f, 0f);
        geom.setShadowMode(RenderQueue.ShadowMode.Receive);
        this.attachChild(geom);
    }

    boolean checkBoundaries(Vector3f position, float margin) {
        boolean outOfBounds = false;
        float eh = edgeLength / 2f;
        if (position.x > eh - margin) {
            position.x = eh - margin;
            outOfBounds = true;
        } else if (position.x < -eh + margin) {
            position.x = -eh + margin;
            outOfBounds = true;
        }
        if (position.z > eh - margin) {
            position.z = eh - margin;
            outOfBounds = true;
        } else if (position.z < -eh + margin) {
            position.z = -eh + margin;
            outOfBounds = true;
        }
        return (outOfBounds);
    }
}
