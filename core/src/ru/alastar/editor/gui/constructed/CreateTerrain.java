package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import ru.alastar.editor.EditorScreen;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;

public class CreateTerrain extends ConstructedGUI {
	
	private static Window createTerrainWindow;

	public CreateTerrain()
	{
		Table createTWindowTable = new Table(); 
		final TextField columns = new TextField("5", GUICore.getSelectedSkin(), "default");
		final TextField rows = new TextField("5",GUICore.getSelectedSkin(), "default");
		columns.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
		rows.setTextFieldFilter(new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter());
		TextButton confirmButton = new TextButton(LanguageManager.getLocalizedMessage("Confirm"), GUICore.getSelectedSkin());
		confirmButton.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.CreateTerrain(Integer.parseInt(columns.getText()), Integer.parseInt(rows.getText()));
				createTerrainWindow.setVisible(false);
                EditorScreen.UpdateGOs();
			}});
		createTWindowTable.add(new Label(" X" + ":", GUICore.getSelectedSkin())).fill();
		createTWindowTable.add(columns).fill();
		createTWindowTable.row();
		createTWindowTable.add(new Label(" Y" + ":", GUICore.getSelectedSkin())).fill();
		createTWindowTable.add(rows).fill();
		createTWindowTable.row();
		createTWindowTable.row();
		createTWindowTable.add(confirmButton).fill();
		
		createTerrainWindow = new Window(LanguageManager.getLocalizedMessage("Terrain"), GUICore.getSelectedSkin());
		createTerrainWindow.setMovable(true);
		createTerrainWindow.setResizeBorder(10);
		createTerrainWindow.pad(5);
		createTerrainWindow.padTop(18);
		createTerrainWindow.add(createTWindowTable);
		createTerrainWindow.setPosition(100, 100);
		createTerrainWindow.setVisible(false);
		createTerrainWindow.pack();
		createTerrainWindow.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
	}
	
	@Override
	public Actor getByName(String s) {
		if(s.equals("window"))
			return createTerrainWindow;
		return null;
	}

	@Override
	public void register(Stage s) {
		s.addActor(createTerrainWindow);
	}

	@Override
	public void show() {
		CreateTerrain.createTerrainWindow.setVisible(true);
		
	}

	@Override
	public void hide() {
		createTerrainWindow.setVisible(false);
	}

}
