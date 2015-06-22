package ru.alastar.editor.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ru.alastar.Engine;
import ru.alastar.editor.EditorScreen;
import ru.alastar.editor.brushes.BrushMode;
import ru.alastar.editor.brushes.BrushType;
import ru.alastar.editor.gui.editors.Editor;
import ru.alastar.game.GameObject;
import ru.alastar.game.Terrain;
import ru.alastar.gui.GUICore;
import ru.alastar.lang.LanguageManager;

import java.lang.reflect.Field;

/**
 * Created by mick on 30.04.15.
 */
public class ObjectManager extends Window {

    public GameObject go;
    private TextField xPosition;
    private TextField yPosition;
    private TextField zPosition;
    private TextField xEuler;
    private TextField yEuler;
    private TextField zEuler;
    private TextField xScale;
    private TextField yScale;
    private TextField zScale;
    private TextField brushRadius;
    private TextField brushStrength;
    private TextField goTag;
    private SelectBox<String> textureName;
    private SelectBox<String> brushType;
    private SelectBox<String> brushMode;
    private SelectBox<String> brushTexture;

    private ScrollPane scroll;

    private TextButton deleteGO;

    private TextButton goToGO;
    private Table windowTable;

    public ObjectManager(String title, Skin skin) {
        super(title, skin);
        build();
    }

    private void build() {
        windowTable = new Table();

        windowTable.setSkin(GUICore.getSelectedSkin());
        windowTable.pad(1);
        windowTable.left();
        windowTable.columnDefaults(1);

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

        deleteGO = new TextButton(("Delete"), GUICore.getSelectedSkin(), "default");
        xPosition = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        xPosition.setTextFieldFilter(digitsFilter);
        xPosition.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getPosition(v);
                float var = v.x;
                try{
                   var = Float.parseFloat(xPosition
                            .getText());
                }
                catch (Exception e)
                {
                    Engine.LogException(e);
                }

                if (!xPosition.getText().isEmpty())
                    EditorScreen.environment.selected.setPosition(new Vector3(var, v.y, v.z));
            }
        });

        goTag = new TextField("", GUICore.getSelectedSkin(), "default");
        goTag.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                if (!goTag.getText().isEmpty())
                    EditorScreen.environment.selected.tag = goTag.getText();
            }
        });

        textureName = new SelectBox<String>(GUICore.getSelectedSkin(), "default");
        textureName.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                //TODO: add more options to the material, and make material editor
               // if(EditorScreen.environment.selected != null)
                   // EditorScreen.environment.selected.setTexture(textureName.getSelected(), TextureAttribute.createDiffuse(EditorScreen.getTexture(textureName.getSelected())));
            }

        });

        brushTexture = new SelectBox<String>(GUICore.getSelectedSkin(), "default");

        brushTexture.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.environment.pushBrushTexture(brushTexture.getSelected());
            }

        });

        brushMode = new SelectBox<String>(GUICore.getSelectedSkin(), "default");
        brushMode.setItems(BrushMode.Edit.toString(), BrushMode.Paint.toString());

        brushMode.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.environment.brushMode = BrushMode.valueOf(brushMode.getSelected());
            }

        });

        brushType = new SelectBox<String>(GUICore.getSelectedSkin(), "default");
        brushType.setItems(BrushType.Quad.toString(), BrushType.Circle.toString());

        brushType.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.environment.setBrushType(BrushType.valueOf(brushType.getSelected()));
            }

        });

        yPosition = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        yPosition.setTextFieldFilter(digitsFilter);
        yPosition.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getPosition(v);

                float var = v.y;
                try{
                    var = Float.parseFloat(xPosition
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }

                if (!yPosition.getText().isEmpty())
                    EditorScreen.environment.selected.setPosition(new Vector3(v.x, var, v.z));
            }
        });

        zPosition = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        zPosition.setTextFieldFilter(digitsFilter);
        zPosition.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getPosition(v);

                float var = v.z;
                try{
                    var = Float.parseFloat(xPosition
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }

                if (!zPosition.getText().isEmpty())
                    EditorScreen.environment.selected.setPosition(new Vector3(v.x, v.y, var));

            }
        });

        xEuler = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        xEuler.setTextFieldFilter(digitsFilter);
        xEuler.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Quaternion rotation = new Quaternion();

                EditorScreen.environment.selected.getRotation(rotation);
                float var = rotation.getYaw();
                try{
                    var = Float.parseFloat(xEuler
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }

                if (!xEuler.getText().isEmpty())
                    EditorScreen.environment.selected.setRotation(new Vector3(var, rotation.getPitch(), rotation.getRoll()));

            }
        });

        yEuler = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        yEuler.setTextFieldFilter(digitsFilter);
        yEuler.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Quaternion rotation = new Quaternion();
                EditorScreen.environment.selected.getRotation(rotation);
                float var = rotation.getPitch();
                try{
                    var = Float.parseFloat(yEuler
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }
                if (!yEuler.getText().isEmpty())
                    EditorScreen.environment.selected.setRotation(new Vector3(rotation.getYaw(), var, rotation.getRoll()));

            }
        });

        zEuler = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        zEuler.setTextFieldFilter(digitsFilter);
        zEuler.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Quaternion rotation = new Quaternion();
                EditorScreen.environment.selected.getRotation(rotation);
                float var = rotation.getRoll();
                try{
                    var = Float.parseFloat(zEuler
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }
                if (!zEuler.getText().isEmpty())
                    EditorScreen.environment.selected.setRotation(new Vector3(rotation.getPitch(), rotation
                            .getYaw(), var));

            }
        });

        xScale = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        xScale.setTextFieldFilter(digitsFilter);
        xScale.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getScale(v);
                float var = v.x;
                try{
                    var = Float.parseFloat(xScale
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }
                if (!xScale.getText().isEmpty())
                    EditorScreen.environment.selected.scale(new Vector3(var, v.y, v.z));

            }
        });

        yScale = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        yScale.setTextFieldFilter(digitsFilter);
        yScale.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getScale(v);
                float var = v.y;
                try{
                    var = Float.parseFloat(yScale
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }
                if (!yScale.getText().isEmpty())
                    EditorScreen.environment.selected.scale(new Vector3(v.x, var, v.z));

            }
        });


        zScale = new TextField("0.0", GUICore.getSelectedSkin(), "default");
        zScale.setTextFieldFilter(digitsFilter);
        zScale.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getScale(v);
                float var = v.z;
                try{
                    var = Float.parseFloat(zScale
                            .getText());
                }
                catch (Exception e)
                {
                   Engine.LogException(e);
                }
                if (!zScale.getText().isEmpty())
                    EditorScreen.environment.selected.scale(new Vector3(v.x, v.y, var));

            }
        });

        goToGO = new TextButton(("GoTo"), GUICore.getSelectedSkin(), "default");
        goToGO.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                ////System.out.println("LOAD MODEL");
                Vector3 v = new Vector3();
                EditorScreen.environment.selected.getPosition(v);
                Vector3 vec = new Vector3(v.x - EditorScreen.environment.camera.position.x, v.y
                        - EditorScreen.environment.camera.position.y, v.z - EditorScreen.environment.camera.position.z);
                vec.add(new Vector3(10, 10, 10));
                ////System.out.println(vec.toString());

               EditorScreen.environment.camera.translate(vec);
                EditorScreen.environment.camera.lookAt(v);
                EditorScreen.environment.camera.update();
            }

        });
        brushRadius = new TextField(Integer.toString(EditorScreen.environment.brushRadius), GUICore.getSelectedSkin(), "default");
        brushRadius.setTextFieldFilter(digitsFilter);
        brushRadius.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                if (!brushRadius.getText().isEmpty())
                {
                    int set = EditorScreen.environment.brushRadius;
                    try{
                        set = Integer.parseInt(brushRadius.getText());
                    }
                    catch(Exception ignored){Engine.LogException(ignored);}
                    EditorScreen.environment.brushRadius = set;
                }
            }
        });

        brushStrength = new TextField(Integer.toString(EditorScreen.environment.brushStrength), GUICore.getSelectedSkin(), "default");
        brushStrength.setTextFieldFilter(digitsFilter);
        brushStrength.setTextFieldListener(new TextField.TextFieldListener() {

            public void keyTyped(TextField textField, char c) {
                if (!brushRadius.getText().isEmpty()) {
                    int set = EditorScreen.environment.brushStrength;
                    try {
                        set = Integer.parseInt(brushStrength.getText());
                    } catch (Exception ignored) {
                        Engine.LogException(ignored);
                    }
                    EditorScreen.environment.brushStrength = set;
                }
            }
        });
        rebuild(null);

        //windowTable.validate();


        scroll = new ScrollPane(windowTable, (ScrollPane.ScrollPaneStyle)GUICore.getStyle(ScrollPane.ScrollPaneStyle.class));
        scroll.setFadeScrollBars(true);
        scroll.setScrollBarPositions(true, true);
        scroll.setOverscroll(true, true);
        scroll.setScrollbarsOnTop(true);
        scroll.setSmoothScrolling(true);
        scroll.setForceScroll(false,false);

        this.add(scroll);
        this.setVisible(false);
        this.setMovable(true);
        this.setResizeBorder(15);
        this.pad(15);
        this.pack();
        this.setPosition(Gdx.graphics.getWidth() / 2 - this.getWidth(), Gdx.graphics.getHeight() - this.getHeight());


        deleteGO.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Engine.getWorld().deleteGO(EditorScreen.environment.selected);
                EditorScreen.environment.selected = null;
                EditorScreen.UpdateGOs();
                EditorScreen.DeactivatePropsWindow();
            }

        });

    }

    public void forTerrain(GameObject go){
        rebuild(go);
        Table container = new Table();
        container.setBackground(GUICore.getSelectedSkin().newDrawable("white", Color.DARK_GRAY));
        container.columnDefaults(6);

        container.add(new Label(("Terrain"), GUICore.getSelectedSkin())).fill();
        container.row();
        container.add(new Label(("Strength"), GUICore.getSelectedSkin())).fill();
        container.add(brushStrength).fill();
        container.add(new Label(("Radius"), GUICore.getSelectedSkin())).fill();
        container.add(brushRadius).fill();
        container.add(new Label(("Type"), GUICore.getSelectedSkin())).fill();
        container.add(brushType).fill();
        container.add(new Label(("Mode"), GUICore.getSelectedSkin())).fill();
        container.add(brushMode).fill();
        container.row();
        container.add(new Label(("TextureI"), GUICore.getSelectedSkin())).fill();
        container.add(brushTexture).fill();
        windowTable.add(container).fill().left();
        this.pack();
    }


    public void rebuild(GameObject go) {
        windowTable.clear();

        Table container = new Table();
        container.setBackground(GUICore.getSelectedSkin().newDrawable("white", Color.DARK_GRAY));
        container.columnDefaults(6);
        container.setBackground(GUICore.getSelectedSkin().newDrawable("white", Color.DARK_GRAY));
        container.columnDefaults(6);
        container.setClip(false);
        container.add(new Label(LanguageManager.getLocalizedMessage("Transform"), GUICore.getSelectedSkin())).maxSize(65).left().fill();
        container.row();
        container.add(new Label(LanguageManager.getLocalizedMessage("Position"), GUICore.getSelectedSkin())).maxSize(65).left().fill();
        container.row();
        container.add(new Label("X:", GUICore.getSelectedSkin())).fill();
        container.add(xPosition).fill();
        container.add(new Label("Y:", GUICore.getSelectedSkin())).fill();
        container.add(yPosition).fill();
        container.add(new Label("Z:", GUICore.getSelectedSkin())).fill();
        container.add(zPosition).fill();
        container.row();
        container.add(new Label(LanguageManager.getLocalizedMessage("Rotation"), GUICore.getSelectedSkin())).fill();
        container.row();
        container.add(new Label("X:", GUICore.getSelectedSkin())).fill();
        container.add(xEuler).fill();
        container.add(new Label("Y:", GUICore.getSelectedSkin())).fill();
        container.add(yEuler).fill();
        container.add(new Label("Z:", GUICore.getSelectedSkin())).fill();
        container.add(zEuler).fill();
        container.row();
        container.add(new Label(LanguageManager.getLocalizedMessage("Scale"), GUICore.getSelectedSkin())).fill();
        container.row();
        container.add(new Label("X:", GUICore.getSelectedSkin())).fill();
        container.add(xScale).fill();
        container.add(new Label("Y:", GUICore.getSelectedSkin())).fill();
        container.add(yScale).fill();
        container.add(new Label("Z:", GUICore.getSelectedSkin())).fill();
        container.add(zScale).fill();
        container.row();
        container.add(new Label(LanguageManager.getLocalizedMessage("Tag") + ":", GUICore.getSelectedSkin())).fill();

        container.add(goTag).fill();
        container.add(new Label(LanguageManager.getLocalizedMessage("Texture Name"), GUICore.getSelectedSkin())).fill();
        container.add(textureName);

        container.row();
        container.add(new Label("Actions:", GUICore.getSelectedSkin())).fill();
        container.row();
        container.add(deleteGO).fill();
        container.add(goToGO).fill();
        windowTable.add(container).fill().left();
        if(go != null && go.getModel() != null)
        {
            windowTable.add(createMaterialEditor(go));
        }
        this.pack();

    }

    private Actor createMaterialEditor(GameObject go) {
        Table container = new Table();
        for(Node n: go.getModel().nodes)
        {

        }
        return container;
    }

    public ObjectManager setObject(GameObject go)
    {
        rebuild(go);
        if(go instanceof Terrain)
        {
            forTerrain(go);
        }
        for(ru.alastar.game.components.Component c: go.getComponents())
        {
            buildForComponent(c);
        }
        return this;
    }

    private void buildForComponent(final ru.alastar.game.components.Component c) {
        //System.out.println(c.getClass().getName());
        windowTable.row();
        if(Editor.hasEditor(c)){
            windowTable.add(Editor.getEditor(c).construct(c)).fill().left();
        }else {
            Table container = new Table();
            container.setBackground(GUICore.getSelectedSkin().newDrawable("white", Color.DARK_GRAY));
            container.columnDefaults(6);
            container.left();
            CheckBox cb = new CheckBox("", (CheckBox.CheckBoxStyle) GUICore.getStyle(CheckBox.CheckBoxStyle.class));
            cb.setWidth(25);
            cb.setHeight(25);
            cb.setChecked(c.getActive());
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    c.setActive(((CheckBox) actor).isChecked());
                }
            });

            container.add(cb).left();
            container.add(new Label(c.getName(), GUICore.getSelectedSkin())).left();
            container.row();

            Class clazz = c.getClass();
            Field[] fieldz = clazz.getFields();
            float tmpF = 0.0f;
            Vector3 tmpV = null;

            for (Field field : fieldz) {
                try {
                    parseField(field, clazz, container);
                    container.row();
                } catch (IllegalAccessException e) {
                   Engine.LogException(e);
                } catch (InstantiationException e) {
                   Engine.LogException(e);
                }
            }
            windowTable.add(container).fill().left();
        }
    }

    private void parseField(final Field field, Class parent, Table container) throws IllegalAccessException, InstantiationException {
        final Class clazz = field.getType();
        Field[] fieldz = clazz.getFields();
        //System.out.println("Parsing: " + field.getName() + ". Type: " + clazz.getName() + ". Fields("+fieldz.length+"):");
        final TextField editField;

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

        for (Field f : fieldz) {
            //System.out.println(f.getName());
        }

        if(fieldz.length > 0 && clazz.getName() != "java.lang.String") {
            for (Field f1 : fieldz) {
                if(f1.getModifiers() != 25)
                    parseField(f1, clazz, container);
            }
        }
        else
        {
            if(clazz.getName() == "int")
            {
                ////System.out.println("Its a simple field: " + field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();


                final Object objectInstance = parent.newInstance();

                int value = (Integer)field.get(objectInstance);

               // //System.out.println("Val: " + value);

                editField = new TextField(Integer.toString(value), (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        int v = 0;
                        try{
                            v = Integer.parseInt(editField.getText());
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.setInt(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
            else if(clazz.getName() == "float"){
                ////System.out.println("Its a simple field: " +field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();

                final Object objectInstance = parent.newInstance();

                final float value = (Float)field.get(objectInstance);

            //    //System.out.println("Val: " + value);

                editField = new TextField(Float.toString(value), (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        float v = value;
                        try{
                            v = Float.parseFloat(editField.getText());
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.setFloat(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
            else if(clazz.getName() == "double"){
                //System.out.println("Its a simple field: " +field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();
                final Object objectInstance = parent.newInstance();

                final double value = (Double)field.get(objectInstance);

                //System.out.println("Val: " + value);

                editField = new TextField(Double.toString(value), (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        double v = value;
                        try{
                            v = Double.parseDouble(editField.getText());
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.setDouble(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
            else if(clazz.getName() == "short"){
                //System.out.println("Its a simple field: " +field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();
                final Object objectInstance = parent.newInstance();

                final short value = (Short)field.get(objectInstance);

                //System.out.println("Val: " + value);

                editField = new TextField(Short.toString(value), (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        short v = value;
                        try{
                            v = Short.parseShort(editField.getText());
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.setShort(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
            else if(clazz.getName() == "byte"){
                //System.out.println("Its a simple field: " +field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();

                final Object objectInstance = parent.newInstance();

                final byte value = (Byte)field.get(objectInstance);

                //System.out.println("Val: " + value);

                editField = new TextField(Byte.toString(value), (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        byte v = value;
                        try{
                            v = Byte.parseByte(editField.getText());
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.setByte(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
            else if(clazz.getName() == "java.lang.String"){
                //System.out.println("Its a simple field: " +field.getName());
                container.add(new Label(field.getName(), GUICore.getSelectedSkin())).left();

                final Object objectInstance = parent.newInstance();

                final String value = (String)field.get(objectInstance);

                //System.out.println("Val: " + value);
                if(value == null)
                editField = new TextField("null", (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));
                else
                    editField = new TextField(value, (TextField.TextFieldStyle)GUICore.getStyle(TextField.TextFieldStyle.class));

                editField.setTextFieldFilter(digitsFilter);
                editField.setTextFieldListener(new TextField.TextFieldListener() {
                    @Override
                    public void keyTyped(TextField textField, char c) {
                        String v = value;
                        try{
                            v = editField.getText();
                        }
                        catch (Exception e)
                        {
                           Engine.LogException(e);
                        }
                        try {
                            field.set(objectInstance, v);
                        } catch (IllegalAccessException e) {
                           Engine.LogException(e);
                        }
                    }
                });
                container.add(editField).left();
            }
        }
    }

    public Actor getTexturesList() {
        return textureName;
    }

    public TextField getxEuler() {
        return xEuler;
    }

    public TextField getyEuler() {
        return yEuler;
    }

    public TextField getzEuler() {
        return zEuler;
    }

    public TextField getxPosition() {
        return xPosition;
    }

    public TextField getyPosition() {
        return yPosition;
    }

    public TextField getzPosition() {
        return zPosition;
    }

    public TextField getgoTag() {
        return goTag;
    }

    public TextField getxScale() {
        return xScale;
    }

    public TextField getyScale() {
        return yScale;
    }

    public TextField getzScale() {
        return zScale;
    }

    public SelectBox<String> getbrushTexture() {
        return brushTexture;
    }
}
