package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;

public class TexturesView extends ConstructedGUI {

	private final Table texPaneContent;

	private static Window texturesWindow;
	
	public TexturesView()
	{
		texPaneContent = new Table();
		ScrollPane texturesPane = new ScrollPane(texPaneContent);
		texturesPane.setStyle((ScrollPaneStyle) GUICore.getStyle(ScrollPaneStyle.class));
		//texturesPane.setFillParent(true);
		texturesPane.setFlickScroll(true);
		texturesPane.setSmoothScrolling(true);
		texturesPane.setScrollingDisabled(false, false);
		
		texturesWindow = new Window(LanguageManager.getLocalizedMessage("Textures"), GUICore.getSelectedSkin());
		texturesWindow.setMovable(true);
		texturesWindow.setResizeBorder(5);
		texturesWindow.pad(5);
		texturesWindow.padTop(18);
		texturesWindow.setSize(150, 500);
		texturesWindow.add(texturesPane);
		texturesWindow.setPosition(Gdx.graphics.getWidth() - texturesWindow.getWidth(), Gdx.graphics.getHeight() - texturesWindow.getHeight());
		
		
	}
	
	@Override
	public Actor getByName(String s) {
		if(s.equals("content"))
			return texPaneContent;
		return null;
	}

	@Override
	public void register(Stage s) {
		s.addActor(texturesWindow);
	}

	@Override
	public void show() {
		texturesWindow.setVisible(true);
	}

	@Override
	public void hide() {
		texturesWindow.setVisible(false);
	}

}
