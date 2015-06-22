package ru.alastar.editor.gui.editors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ru.alastar.Engine;
import ru.alastar.game.components.Rigidbody3D;
import ru.alastar.gui.GUICore;

/**
 * Created by mick on 02.05.15.
 */
public class RigidBodyEditor extends Editor{

    @Override
    public Table construct(final ru.alastar.game.components.Component c) {
        final Rigidbody3D rigid = (Rigidbody3D)c;

        Table table = new Table();
        table.left();
        table.setBackground(GUICore.getSelectedSkin().newDrawable("white", Color.DARK_GRAY));
        CheckBox ecb = new CheckBox("", (CheckBox.CheckBoxStyle) GUICore.getStyle(CheckBox.CheckBoxStyle.class));
        ecb.setWidth(25);
        ecb.setHeight(25);
        ecb.setChecked(c.getActive());
        ecb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                c.setActive(((CheckBox) actor).isChecked());
            }
        });

        table.add(ecb);
        table.add(new Label("RigidBody", GUICore.getSelectedSkin())).left();
        table.row();
        TextField.TextFieldFilter digitsFilter = new TextField.TextFieldFilter() {

            @Override
            public boolean acceptChar(TextField textField, char c) {
                if (c != '.' && c != '-')
                    if (!Character.isDigit(c))
                        return false;
                if (c == '-') {
                    return !(textField.getText().toCharArray().length != 0 && textField.getText().toCharArray()[0] == '-');
                }
                return !(c == '.' && textField.getText().contains("."));
            }

        };
        final float mass = rigid.getInfo().getMass();

        final TextField edit = new TextField(Float.toString(mass), GUICore.getSelectedSkin());

        edit.setTextFieldFilter(digitsFilter);

        edit.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                float v = mass;
                try{
                    v = Float.parseFloat(edit.getText());
                }
                catch (Exception e)
                {
                    Engine.LogException(e);
                }
                rigid.setMass(v);
            }
        });
        boolean isKinematic = rigid.getInfo().isKinematic();
        final CheckBox cb = new CheckBox("", GUICore.getSelectedSkin());
        cb.setChecked(rigid.getInfo().isKinematic());
        cb.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rigid.setKinematic(cb.isChecked());
            }
        });
        table.add(new Label("Mass: ", GUICore.getSelectedSkin())).left();
        table.add(edit).left();
        table.row();
        table.add(new Label("isKinematic: ", GUICore.getSelectedSkin())).left();
        table.add(cb).left();
        table.pack();

        return table;
    }
}
