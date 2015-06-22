package ru.alastar.editor.gui.editors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Hashtable;

/**
 * Created by mick on 02.05.15.
 */
public abstract class Editor {

    public static Hashtable<Class, Editor> editors = new Hashtable<Class, Editor>();

    public static void addEditor(Class c, Editor e)
    {
        editors.put(c, e);
    }

    public abstract Table construct(ru.alastar.game.components.Component c);

    public static Editor getEditor(ru.alastar.game.components.Component c) {
        return editors.get(c.getClass());
    }

    public static boolean hasEditor(ru.alastar.game.components.Component c) {
        if(editors.containsKey(c.getClass()))
            return true;
        return false;
    }
}
