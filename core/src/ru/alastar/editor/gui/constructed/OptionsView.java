package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import ru.alastar.Engine;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.gui.elements.TabSwitcher;
import ru.alastar.lang.LanguageManager;
import ru.alastar.utils.Options;

import java.io.IOException;

/**
 * Created by mick on 02.05.15.
 */
public class OptionsView extends ConstructedGUI {

    private Window window;
    private TabSwitcher tab;
    public OptionsView()
    {
        tab = new TabSwitcher(500, 400);
        Table lang = tab.createTab("Lang");
        Table about = tab.createTab("About");
        about.add(new Label(LanguageManager.getLocalizedMessage("About"), GUICore.getSelectedSkin()));

        createLanguagePage(lang);
        tab.setActive("Lang");

        TextButton close = new TextButton(LanguageManager.getLocalizedMessage("Close"), GUICore.getSelectedSkin());
        close.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
                Options.save();
            }
        });


        window = new Window(LanguageManager.getLocalizedMessage("Options"), GUICore.getSelectedSkin());
        window.setMovable(true);
        window.setResizeBorder(15);
        window.pad(15, 5, 5, 5);
        window.setVisible(false);
        window.setSize(600, 500);
        window.add(tab).minSize(570, 460).fill();
        window.row();
        window.add(close).left();
        window.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
    }

    private void createLanguagePage(final Table lang) {
        final SelectBox<String> langs = new SelectBox<String>(GUICore.getSelectedSkin());
        langs.setItems("ru", "en");
        langs.setSelected((String)Options.getOption("language"));
        langs.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    LanguageManager.init(langs.getSelected());
                    Options.setOption("language", langs.getSelected());
                } catch (IOException e) {
                    Engine.LogException(e);
                }
            }
        });
        final TextField fontSize = new TextField(Integer.toString((Integer)Options.getOption("fontSize")), GUICore.getSelectedSkin());
        fontSize.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        fontSize.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                int var = (Integer) Options.getOption("fontSize");
                try {
                    var = Integer.parseInt(fontSize.getText());
                } catch (Exception e) {
                    Engine.LogException(e);
                }
                Options.setOption("fontSize", var);
            }
        });
        lang.top();
        lang.left();
        lang.add(new Label( LanguageManager.getLocalizedMessage("Language"), GUICore.getSelectedSkin())).left();
        lang.add(langs).fill();
        lang.row();
        lang.add(new Label(LanguageManager.getLocalizedMessage("FontSize"), GUICore.getSelectedSkin())).fill().left();
        lang.add(fontSize);
    }

    @Override
    public Actor getByName(String s) {
        return null;
    }

    @Override
    public void register(Stage s) {
        s.addActor(window);
    }

    @Override
    public void show() {
        window.setVisible(true);
    }

    @Override
    public void hide() {
        window.setVisible(false);
    }
}
