package ru.alastar.editor.gui.constructed;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import ru.alastar.editor.gui.ObjectManager;
import ru.alastar.game.GameObject;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;

public class PropsWindow extends ConstructedGUI {
	
	private ObjectManager manager;
	
	public PropsWindow()
	{
		manager = new ObjectManager(LanguageManager.getLocalizedMessage("GO"), GUICore.getSelectedSkin());
		manager.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
	}

	public void forTerrain(){
		manager.forTerrain(manager.go);
	}

	@Override
	public Actor getByName(String s) {
		if(s.equals("texturesList"))
			return manager.getTexturesList();
		if(s.equals("window"))
			return manager;
		if(s.equals("xEuler"))
			return manager.getxEuler();
		if(s.equals("yEuler"))
			return manager.getyEuler();
		if(s.equals("zEuler"))
			return manager.getzEuler();
		if(s.equals("xPosition"))
			return manager.getxPosition();
		if(s.equals("yPosition"))
			return manager.getyPosition();
		if(s.equals("zPosition"))
			return manager.getzPosition();
		if(s.equals("goTag"))
			return manager.getgoTag();
		if(s.equals("xScale"))
			return manager.getxScale();
		if(s.equals("yScale"))
			return manager.getyScale();
		if(s.equals("zScale"))
			return manager.getzScale();
		if(s.equals("brushTexture"))
			return manager.getbrushTexture();
		return null;
	}

	@Override
	public void register(Stage s) {
		s.addActor(manager);
	}

	public void rebuild() {
	manager.rebuild(manager.go);

	}
	
	@Override
	public void show() {
		manager.setVisible(true);

	}

	public void hide() {
		this.rebuild();
		manager.setVisible(false);
	}

	public void setObject(GameObject object) {
		this.manager.setObject(object);
	}
}
