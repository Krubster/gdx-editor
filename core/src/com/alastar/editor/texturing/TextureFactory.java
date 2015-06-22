package com.alastar.editor.texturing;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class TextureFactory {

	private static final Rectangle tmpR1 = new Rectangle();
    private static final Circle circle = new Circle();
    private static final Pixmap tmp = new Pixmap(10,10, Pixmap.Format.RGBA8888);
	public static Pixmap paint(Pixmap tex, float x, float z, float f,
			int brushStrength, Pixmap texToDraw, int meshX, int meshY, BrushForm type) {
		int resolution = tex.getHeight() / 32;

		int left = meshX * tex.getWidth();
		int bottom = meshY * tex.getHeight();
		int top = (meshY + 1) * tex.getHeight();
		int right = (meshX + 1) * tex.getWidth();

		float pixX = x * resolution;
		float pixY = z * resolution;

        Color col = new Color(0,0,0,1);
		int i, pix;
		for(i = left; i <= right; ++i)
		{
			int j;
			for(j = bottom; j <= top; ++j)
			{				
				if(inRange(i, j, pixX, pixY, f * resolution, type))
				{
                    pix = texToDraw.getPixel(getInBounds(i, texToDraw.getWidth()), getInBounds(j, texToDraw.getHeight()));
                    col = new Color(pix);
                    if(brushStrength / Vector2.dst(pixX, pixY, i, j) < 1)
                    col.a =  (brushStrength / Vector2.dst(pixX, pixY, i, j));
                    else
                    col.a = 1;
                    tmp.setColor(col);
                    tmp.fill();
					tex.drawPixel(i - left, j - bottom, tmp.getPixel(1,1));
				}
			}
		}
		return tex;
	}

	private static boolean inRange(int i, float j, float pixX, float pixY,
			float r, BrushForm form) {
        if(form.equals(BrushForm.QUAD)) {
            return tmpR1.set(pixX - r, pixY - r, r * 2, r * 2).contains(i, j);
        }else
        {
            circle.set(pixX, pixY, r);
            return circle.contains(i, j);
        }
    }

	private static int getInBounds(int i, int width) {
		return i - (int)Math.floor(i / width) * width;
	}	
}
