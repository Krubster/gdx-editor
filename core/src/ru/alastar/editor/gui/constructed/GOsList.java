package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;

public class GOsList extends ConstructedGUI {
	private final Window gosWindow;
    Tree tree;
	public GOsList(){
		tree = new Tree(GUICore.getSelectedSkin());
        tree.setPadding(5);
        ScrollPane gosPane = new ScrollPane(tree);
        gosPane.setStyle((ScrollPaneStyle) GUICore.getStyle(ScrollPaneStyle.class));
        //texturesPane.setFillParent(true);
        gosPane.setFlickScroll(true);
        gosPane.setSmoothScrolling(true);
        gosPane.setScrollingDisabled(false, false);

        gosWindow = new Window(LanguageManager.getLocalizedMessage("Object"), GUICore.getSelectedSkin());
		gosWindow.setMovable(true);
		gosWindow.setResizeBorder(10);
		gosWindow.pad(5);
		gosWindow.padTop(18);
		gosWindow.setSize(150, 500);
		gosWindow.add(gosPane);
		gosWindow.setPosition(5, 100);
	}
	
	@Override
	public Actor getByName(String s) {
		if(s.equals("content"))
			return tree;
		return null;
	}

	@Override
	public void register(Stage s) {
		s.addActor(gosWindow);
	}

	@Override
	public void show() {
		this.gosWindow.setVisible(true);
		
	}

	@Override
	public void hide() {
		gosWindow.setVisible(false);
	}

}
