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

public class ModelView extends ConstructedGUI {

	private final Window modelWindow;
	private final Table paneContent;
	public ModelView()
	{
		paneContent = new Table();
		ScrollPane pane = new ScrollPane(paneContent);
		pane.setStyle((ScrollPaneStyle) GUICore.getStyle(ScrollPaneStyle.class));
		pane.setFlickScroll(true);
		pane.setSmoothScrolling(true);
		pane.setScrollingDisabled(false, true);

		modelWindow = new Window(LanguageManager.getLocalizedMessage("Models"), GUICore.getSelectedSkin());
		modelWindow.setPosition(0, 0);
		modelWindow.setMovable(false);
		modelWindow.setResizeBorder(5);
		modelWindow.pad(5);
		modelWindow.padTop(18);
		modelWindow.setSize(Gdx.graphics.getWidth(), 50);
		modelWindow.add(pane);
	}
	@Override
	public Actor getByName(String s){
		if(s.equals("content"))
		return paneContent;
		
		return null;
	}
	
	@Override
	public void register(Stage s)
	{
		s.addActor(modelWindow);
	}

	@Override
	public void show() {
		this.modelWindow.setVisible(true);
		
	}

	@Override
	public void hide() {
		this.modelWindow.setVisible(false);
	}

}
