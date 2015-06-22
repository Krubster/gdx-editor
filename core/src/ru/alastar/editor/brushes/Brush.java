package ru.alastar.editor.brushes;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector3;
import ru.alastar.game.ChunkModel;
import ru.alastar.game.GameObject;

public abstract class Brush {
	public abstract Pixmap paint(int brushRadius, int brushStrength, Pixmap to,
			Vector3 intersectPoint, Pixmap tex, int x, int y);

    public abstract void deform(GameObject go, ChunkModel n, Mesh m,int meshX, int meshY, float[] vertices, int brushStrength, int brushRadius, Vector3 intersectPoint);

    public abstract boolean inRange(float centerX, float centerY, int radius, float pointX, float pointY);
}
