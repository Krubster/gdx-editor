package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ru.alastar.Engine;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;
import ru.alastar.utils.Console;

public class ConsoleView extends ConstructedGUI {

	private final Window consoleView;
	private TextField command;
	private final TextArea log;

	public ConsoleView()
	{
		Table content = new Table();
		TextButton send = new TextButton(LanguageManager.getLocalizedMessage("Confirm"), GUICore.getSelectedSkin());
		send.pad(1);
		send.addListener(new ChangeListener() {

			public void changed(ChangeEvent event, Actor actor) {
				Console.pushCommand(command.getText());
				command.setText("");
			}

		});


		command = new TextField("", GUICore.getSelectedSkin(), "default");
		command.setFocusTraversal(false);
		command.setTextFieldListener(new TextFieldListener() {

			public void keyTyped(TextField textField, char c) {
				if(c == '\n' || c == '\r')
				{
					Console.pushCommand(command.getText());
					command.setText("");
				}
			}
		});

		log = new TextArea("", GUICore.getSelectedSkin(), "default");
		//log.setSize(500, 465);
		log.setPrefRows(20);
		log.setLayoutEnabled(true);

		content.add(log).colspan(2).minSize(500, 465);
		content.row();
		content.add(command).minWidth(445).pad(2);
		content.add(send).minWidth(35);

		consoleView = new Window(LanguageManager.getLocalizedMessage("Console"), GUICore.getSelectedSkin(), "default");
		consoleView.add(content);
		consoleView.setVisible(false);

		WindowStyle style = new WindowStyle();
		style.stageBackground = GUICore.getSelectedSkin().newDrawable("white", new Color(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.5f));
		style.titleFont = GUICore.getFont();
		style.titleFontColor = Color.CYAN;
		style.background = GUICore.getSelectedSkin().newDrawable("white", Color.GRAY);

		consoleView.setStyle(style);
		consoleView.setMovable(true);
		consoleView.setResizeBorder(15);
		consoleView.pad(15);
		consoleView.pack();
		consoleView.setPosition(Gdx.graphics.getWidth() - 400, Gdx.graphics.getHeight() - consoleView.getHeight());

		
	}
	
	@Override
	public Actor getByName(String s) {
		if(s.equals("log"))
			return log;
		return null;
	}

	@Override
	public void register(Stage s) {
		s.addActor(this.consoleView);
	}

	public void switchView() {
		this.consoleView.setVisible(!this.consoleView.isVisible());
		if(consoleView.isVisible())
            Engine.getStage().setKeyboardFocus(command);
		else
            Engine.getStage().unfocusAll();
	}


	@Override
	public void show() {
		this.consoleView.setVisible(true);
		
	}

	@Override
	public void hide() {
		this.consoleView.setVisible(false);
	}

	public void write(String s) {
		this.log.setText(s);
		log.setCursorPosition(log.getText().length() - 1);
	}
}
