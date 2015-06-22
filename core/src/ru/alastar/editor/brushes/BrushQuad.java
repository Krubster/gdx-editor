package ru.alastar.editor.brushes;

import com.alastar.editor.texturing.BrushForm;
import com.alastar.editor.texturing.TextureFactory;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import ru.alastar.editor.EditorEnvironment;
import ru.alastar.editor.EditorScreen;
import ru.alastar.game.ChunkModel;
import ru.alastar.game.GameObject;
import ru.alastar.game.Terrain;

public class BrushQuad extends Brush {

    private static final Rectangle tmp = new Rectangle();

	@Override
	public Pixmap paint(int brushRadius, int brushStrength, Pixmap to,
			Vector3 intersectPoint, Pixmap texture, int x, int y) {
		return TextureFactory.paint(to,
				intersectPoint.x, intersectPoint.z,
				brushRadius, brushStrength, texture, x, y, BrushForm.QUAD);
	}

    @Override
    public void deform(GameObject go, ChunkModel n, Mesh m, int meshX, int meshY, float[] vertices, int brushStrength, int brushRadius, Vector3 intersectPoint) {
        int c;
        float[] changed = new float[1];
        for (c = 0; c < vertices.length; c = c + EditorEnvironment.vertexSize) {
            if (inRange(intersectPoint.x, intersectPoint.z, brushRadius, vertices[c] + meshX * 32, vertices[c+2] + meshY * 32)) {/*
                vertices[c + 1] += brushStrength * 0.5f * (1 / Vector2.dst(intersectPoint.x, intersectPoint.z, vertices[c] + meshX * 32, vertices[c + 2] + meshY * 32));;*/
                changed[0] = vertices[c + 1] + brushStrength * 0.5f;
                m.updateVertices(c + 1, changed);
                if(EditorScreen.isUsePhysics())
                    n.updatePhysicShape = true;
                ((Terrain)go).needsRecalculate = true;
            }
        }
    }

    @Override
    public boolean inRange(float centerX, float centerY, int radius, float pointX, float pointY) {
        if(tmp.set((int)(centerX - radius), (int)(centerY - radius), radius * 2, radius * 2).contains(pointX, pointY))
            return true;
        return false;
    }


}
