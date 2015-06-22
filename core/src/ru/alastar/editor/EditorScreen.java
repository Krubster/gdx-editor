package ru.alastar.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ru.alastar.Engine;
import ru.alastar.editor.gui.constructed.*;
import ru.alastar.editor.gui.editors.Editor;
import ru.alastar.editor.gui.editors.RigidBodyEditor;
import ru.alastar.game.GWorld;
import ru.alastar.game.GameObject;
import ru.alastar.game.Terrain;
import ru.alastar.game.components.Rigidbody;
import ru.alastar.gui.GUICore;
import ru.alastar.gui.elements.TopMenu;
import ru.alastar.utils.FileManager;
import ru.alastar.utils.ObjectPooler;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class EditorScreen implements InputProcessor, Screen {

    public static final String version = "0.1.0v";
	private static final com.badlogic.gdx.math.Quaternion q = new Quaternion(0,0,0,0);
	public static  final Random random = new Random();
	public static boolean simulate = false;
	private final Vector3 moveMod = new Vector3();
	private boolean moving;
	public static EditorEnvironment environment;
	private static final Vector3 tmpV1 = new Vector3();
	private static final Vector3 tmpV2 = new Vector3();
	private static final Vector3 tmpV3 = new Vector3();
	private static boolean usePhysics = false;

    public EditorScreen() {
        Engine.setUseBulletPhysics(true);
        Engine.setDoPhysics(true);
        Engine.setDraw3D(true);
        Engine.setUseLZMA(false);
		Engine.set_clearColor(Color.TEAL);
        Engine.setDebug(true);
        Engine.setFarCulling(true);
        Engine.init();

		environment = new EditorEnvironment();

		GUICore.CreateDefaultSkin();

		GUICore.addGUI("CreateTerrain",
				new ru.alastar.editor.gui.constructed.CreateTerrain())
				.register(Engine.getStage());
		GUICore.addGUI("Options",
				new OptionsView())
				.register(Engine.getStage());

		GUICore.addGUI("GOsList", new GOsList()).register(Engine.getStage());
		GUICore.addGUI("MainWindow", new MainWindow()).register(
				Engine.getStage());
		GUICore.addGUI("ModelView", new ModelView())
				.register(Engine.getStage());
		GUICore.addGUI("PropsWindow", new PropsWindow()).register(
				Engine.getStage());
		GUICore.addGUI("TexturesView", new TexturesView()).register(
				Engine.getStage());
		GUICore.addGUI("ConsoleView", new ConsoleView()).register(
				Engine.getStage());

		Editor.addEditor(Rigidbody.class, new RigidBodyEditor());

		checkFolder();

		Gdx.input.setInputProcessor(this);
	}

	public static void CreateTerrain(int parseInt, int parseInt2) {
		Vector3 position = new Vector3();
		position = environment.camera.direction.cpy();

		position.scl(10f);
		position = environment.camera.position.cpy().add(position);
		Terrain terrain = new Terrain(parseInt, parseInt2, position);
	}

	public static void LoadTexture(File selectedFile) {
		try {
			Files.copy(
					selectedFile.toPath(),
					Paths.get(System.getProperty("user.dir") + "/textures/"
							+ selectedFile.getName()));
			environment.textures.put("/textures/" + selectedFile.getName(),
					loadTexture(Gdx.files.absolute(selectedFile
							.getAbsolutePath())));
			UpdateTextures();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static boolean isUsePhysics() {
		return usePhysics;
	}

	public static void setUsePhysics(boolean usePhysics) {
		EditorScreen.usePhysics = usePhysics;
	}

	private void checkFolder() {
		File dir = new File("models");
		if (!dir.exists())
			dir.mkdir();
		else {
			for (File f : dir.listFiles()) {
				environment.models.put(ripExtension(f.getName()).toLowerCase(),
						environment.modelLoader.loadModel(Gdx.files.absolute(f
								.getAbsolutePath())));
			}
			UpdateModels();
		}
		dir = new File("textures");
		if (!dir.exists())
			dir.mkdir();
		else {
			for (File f : dir.listFiles()) {
				environment.textures.put(ripExtension(f.getName())
						.toLowerCase(), loadTexture(Gdx.files.absolute(f
						.getAbsolutePath())));
			}
			Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			environment.textures.put("None", new Texture(pixmap));

			UpdateTextures();
		}
	}

	@SuppressWarnings("unchecked")
	private static void UpdateTextures() {
		GUICore.getByName("TexturesView").getByName("content").clear();
		GUICore.getByName("PropsWindow").getByName(
				"texturesList").clear();

		Label label = new Label("", GUICore.getSelectedSkin());
		Image img = new Image();
		for (final String name : environment.textures.keySet()) {
			label = new Label(name, GUICore.getSelectedSkin());
			img = new Image(environment.textures.get(name));
			img.setSize(75, 75);

			((Table) GUICore.getByName("TexturesView").getByName("content"))
					.add(img).maxSize(75);
			((Table) GUICore.getByName("TexturesView").getByName("content"))
					.add(label).fill();
			((Table) GUICore.getByName("TexturesView").getByName("content"))
					.row();

		}

		((Table) GUICore.getByName("TexturesView").getByName("content")).pack();
		String[] arr = new String[environment.textures.keySet().size()];
		environment.textures.keySet().toArray(arr);
		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"texturesList")).setItems(arr);

		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"texturesList")).pack();
		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"brushTexture")).setItems(arr);

		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"brushTexture")).pack();
		((Window) GUICore.getByName("PropsWindow").getByName("window")).pack();
	}

	private static Texture loadTexture(FileHandle absolute) {
		return new Texture(absolute);
	}

	private static void UpdateModels() {
		GUICore.getByName("ModelView").getByName("content").clear();
		TextButton modelBtn;
		for (final String name : environment.models.keySet()) {
			modelBtn = new TextButton(
					name.split("/")[name.split("/").length - 1],
					GUICore.getSelectedSkin(), "default");
			modelBtn.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// System.out.println("Add model: " + name);
					CreateGO(name);
				}
			});
			((Table) GUICore.getByName("ModelView").getByName("content")).add(
					modelBtn).fill();
		}
		// modelWindow.pack();

	}

	public static void LoadModel(File selectedFile) {
		try {
			if (getExtension(selectedFile).equals(".g3dj")) {
				Files.copy(
						selectedFile.toPath(),
						Paths.get(System.getProperty("user.dir") + "/models/"
								+ selectedFile.getName()));
				environment.models.put("/models/" + selectedFile.getName(),
						environment.modelLoader.loadModel(Gdx.files
								.absolute(selectedFile.getAbsolutePath())));

				UpdateModels();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getExtension(File selectedFile) {
		String s = selectedFile.getPath();
		s= s.substring( s.lastIndexOf('.'));
		//System.out.println(s);
		return 	s;
	}

	public static String ripExtension(String name) {
		//System.out.println(s);
		return 	name.substring(0, name.lastIndexOf('.'));
	}

	public static void UpdateGOs() {
        ((Tree) GUICore.getByName("GOsList").getByName("content")).clearChildren();
        GUICore.getByName("GOsList").getByName("content").clear();
		Label label = new Label("", GUICore.getSelectedSkin());
        Tree.Node treeNode;
		for (final GameObject go : Engine.getWorld().instances) {
            if(go.getParent() == null && go != environment.xGO && go != environment.yGO && go != environment.zGO && go != environment.zeroGO){
			label = new Label(go.tag, GUICore.getSelectedSkin());
            treeNode = new Tree.Node(label);
			((Tree) GUICore.getByName("GOsList").getByName("content")).add(
                    treeNode);
			label.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    setSelected(go);
                }
            });
                if(go.getChildren().size() > 0)
                buildForChild(go, treeNode);
		    }
        }
        ((Tree) GUICore.getByName("GOsList").getByName("content")).expandAll();
    }

    private static void buildForChild(final GameObject go, Tree.Node treeNode) {
        Label label;
        Tree.Node childNode;
        for(final GameObject o: go.getChildren())
        {
            label = new Label(o.tag, GUICore.getSelectedSkin());
            childNode = new Tree.Node(label);
            label.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    setSelected(o);
                }
            });
            treeNode.add(childNode);

            if(o.getChildren().size() > 0)
                buildForChild(o, childNode);
        }
    }

    public static Texture getTexture(String texName) {
		return environment.textures.get(texName);
	}

	public static Model getModel(String mODEL_NAME) {
		return environment.models.get(mODEL_NAME);
	}

	private static void setSelected(GameObject value) {
		if (environment.selected == value)
			return;
		if (environment.selected != null) {
			DeactivatePropsWindow();
		}
		environment.selected = value;
		if (environment.selected != null) {
			ActivatePropsWindow(environment.selected);
		}
	}

	public static void resetTransform(GameObject g)
	{
		tmpV1.set(0, 0, 0);
		g.getTransform().getTranslation(tmpV1);
		//System.out.println(tmpV1.toString());
		((TextField) GUICore.getByName("PropsWindow").getByName("xPosition"))
				.setText(Float.toString(tmpV1.x));
		((TextField) GUICore.getByName("PropsWindow").getByName("yPosition"))
				.setText(Float.toString(tmpV1.y));
		((TextField) GUICore.getByName("PropsWindow").getByName("zPosition"))
				.setText(Float.toString(tmpV1.z));

		g.getTransform().getScale(tmpV1);
		((TextField) GUICore.getByName("PropsWindow").getByName("xScale"))
				.setText(Float.toString(tmpV1.x));
		((TextField) GUICore.getByName("PropsWindow").getByName("yScale"))
				.setText(Float.toString(tmpV1.y));
		((TextField) GUICore.getByName("PropsWindow").getByName("zScale"))
				.setText(Float.toString(tmpV1.z));

		((TextField) GUICore.getByName("PropsWindow").getByName("goTag"))
				.setText(g.tag);
		// modelName.setText(g.MODEL_NAME);
		/*
		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"texturesList")).setSelected
        */

		q.set(0,0,0,0);
		g.getTransform().getRotation(q);

		((TextField) GUICore.getByName("PropsWindow").getByName("xEuler"))
				.setText(Float.toString(q.getYaw()));
		((TextField) GUICore.getByName("PropsWindow").getByName("yEuler"))
				.setText(Float.toString(q.getPitch()));
		((TextField) GUICore.getByName("PropsWindow").getByName("zEuler"))
				.setText(Float.toString(q.getRoll()));

	}

	@SuppressWarnings("unchecked")
	public static void ActivatePropsWindow(GameObject g) {
		tmpV1.set(0, 0, 0);
		g.getTransform().getTranslation(tmpV1);
		//System.out.println(tmpV1.toString());
		((TextField) GUICore.getByName("PropsWindow").getByName("xPosition"))
				.setText(Float.toString(tmpV1.x));
		((TextField) GUICore.getByName("PropsWindow").getByName("yPosition"))
				.setText(Float.toString(tmpV1.y));
		((TextField) GUICore.getByName("PropsWindow").getByName("zPosition"))
				.setText(Float.toString(tmpV1.z));

		g.getTransform().getScale(tmpV1);
		((TextField) GUICore.getByName("PropsWindow").getByName("xScale"))
				.setText(Float.toString(tmpV1.x));
		((TextField) GUICore.getByName("PropsWindow").getByName("yScale"))
				.setText(Float.toString(tmpV1.y));
		((TextField) GUICore.getByName("PropsWindow").getByName("zScale"))
				.setText(Float.toString(tmpV1.z));

		((TextField) GUICore.getByName("PropsWindow").getByName("goTag"))
				.setText(g.tag);
		/* modelName.setText(g.MODEL_NAME);
		((SelectBox<String>) GUICore.getByName("PropsWindow").getByName(
				"texturesList")).setSelected(g.TEXTURE_NAME);
        */
		q.set(0,0,0,0);
		g.getTransform().getRotation(q);

		((TextField) GUICore.getByName("PropsWindow").getByName("xEuler"))
				.setText(Float.toString(q.getYaw()));
		((TextField) GUICore.getByName("PropsWindow").getByName("yEuler"))
				.setText(Float.toString(q.getPitch()));
		((TextField) GUICore.getByName("PropsWindow").getByName("zEuler"))
				.setText(Float.toString(q.getRoll()));
		if (g.tag.equals("terrain"))
			((PropsWindow) GUICore.getByName("PropsWindow")).forTerrain();
		else
			((PropsWindow) GUICore.getByName("PropsWindow")).rebuild();
		((PropsWindow) GUICore.getByName("PropsWindow")).show();

		((PropsWindow) GUICore.getByName("PropsWindow")).setObject(g);


		((TopMenu)GUICore.getByName("MainWindow").getByName("window")).enable("GO");
        environment.zGO.setActive(true);
        environment.xGO.setActive(true);
        environment.yGO.setActive(true);

	}

	public static void DeactivatePropsWindow() {
		((PropsWindow) GUICore.getByName("PropsWindow")).hide();
		((TopMenu)GUICore.getByName("MainWindow").getByName("window")).disable("GO");
        environment.zGO.setActive(false);
        environment.xGO.setActive(false);
        environment.yGO.setActive(false);
	}

	private static void saveWorld(GWorld w, String n) {
		OutputStream out = FileManager.writeInFile(n);
		try {
			FileManager.putInt(out, w.id);
			FileManager.putInt(out, w.version);

			FileManager.putInt(out, w.instances.size());
			for (GameObject go : w.instances) {
				go.saveTo(out);
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void CreateGO(String mName) {
		GameObject go = null;
		if(ObjectPooler.hasObjects(GameObject.class)) {
			go = (GameObject) ObjectPooler.getFreeObject(GameObject.class);
			go.setModel(new ModelInstance(getModel(mName)), mName);
			go.setTag("GAME OBJECT");
			go.setActive(true);
			go.setFromPool();
		}
		else
			go = new GameObject(getModel(mName), "GAME OBJECT", mName);
		Vector3 position = new Vector3();
		position = environment.camera.direction.cpy();

		position.scl(10f);

		go.setPosition(environment.camera.position.cpy().add(position));
		UpdateGOs();

	}

	public static void LoadScene(File selectedFile) {
		Engine.loadWorld(selectedFile.getAbsolutePath());
		//System.out.println(environment.world.instances.size);
	}

	public static void SaveScene(File selectedFile) {
		Engine.saveWorld(Engine.getWorld(), selectedFile.getAbsolutePath() + ".scn");
	}

	@Override
	public void render(float d) {
        Engine.render(d);
        environment.render();

    }

	@Override
	public void resize(int width, int height) {
		environment.camera.update();
		environment.camera.viewportHeight = height;
		environment.camera.viewportWidth = width;
		environment.camera.update();
		Engine.getStage().getViewport().update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		environment.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
        Engine.keyDown(keycode);
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
        Engine.keyUp(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		Engine.keyTyped(character);
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Engine.touchDown(screenX, screenY, pointer, button);
            environment.touchDown(screenX, screenY);
            checkMoving(screenX, screenY);
		return environment.selected != null;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		environment.scheduling = false;
		if (environment.selected != null
				&& environment.selected.tag.equals("terrain") && ((Terrain)environment.selected).needsRecalculate)
			((Terrain)environment.selected).calculate();

		if (!Engine.getStage().touchUp(screenX, screenY, pointer, button)) {
			Engine.touchUp(screenX,screenY,pointer,button);
			if (environment.selected != null) {
				return true;
			}
		}
		setMoving(new Vector3(0, 0, 0), false);
		return false;
	}

	private void setMoving(Vector3 vector3, boolean t) {
		moveMod.set(vector3);
		moving = t;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (Gdx.input.getX() > Gdx.graphics.getWidth() - 10) {
			Gdx.input.setCursorPosition(0, Gdx.input.getY());
		} else if (Gdx.input.getX() < 10) {
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth(),
					Gdx.input.getY());
		}
		if (Gdx.input.getY() > Gdx.graphics.getHeight() - 10) {
			Gdx.input.setCursorPosition(Gdx.input.getX(), 10);
		} else if (Gdx.input.getY() < 10) {
			Gdx.input.setCursorPosition(Gdx.input.getX(),
					Gdx.graphics.getHeight() - 10);
		}

		if (!Engine.getStage().touchDragged(screenX, screenY, pointer)) {
			if (moving && environment.selected != null) {

				tmpV1.set(moveMod.x * getNormDelta() * environment.mod,
						moveMod.y * getNormDelta() * environment.mod, moveMod.z
								* getNormDelta() * environment.mod);
				tmpV2.set(0, 0, 0);
				environment.selected.getTransform().getTranslation(tmpV2);
				tmpV2.add(tmpV1);
                if(environment.transformation == 0)
				    environment.selected.setPosition(tmpV2);
                else if (environment.transformation == 1)
                    environment.selected.rotateBy(tmpV1);
                else
                    environment.selected.scale(tmpV1);

                ActivatePropsWindow(environment.selected);

			} else if (environment.touchDown(screenX, screenY)
					&& !environment.scheduling) {
                Engine.touchDragged(screenX, screenY, pointer);
			}
		}
		return true;
	}

	private void checkMoving(int screenX, int screenY) {

		tmpV1.set(0, 0, 0);
		tmpV2.set(0, 0, 0);
		tmpV3.set(0, 0, 0);

		Ray ray = environment.camera.getPickRay(screenX, screenY);

		environment.xGO.getTransform().getTranslation(tmpV3);
		environment.yGO.getTransform().getTranslation(tmpV1);
		environment.zGO.getTransform().getTranslation(tmpV2);

		if (Intersector.intersectRaySphere(ray, tmpV3, environment.radius, null)) {
			setMoving(tmpV1.set(0.2f, 0, 0), true);
		} else if (Intersector.intersectRaySphere(ray, tmpV1, environment.radius, null)) {
			setMoving(tmpV1.set(0, 0.2f, 0), true);
		} else if (Intersector.intersectRaySphere(ray, tmpV2, environment.radius, null)) {
			setMoving(tmpV1.set(0, 0, 0.2f), true);
		} else {
			setMoving(tmpV1.set(0, 0, 0), false);
			// TODO: add deselect button
		}
	}

	private float getNormDelta() {
		int modifier = 1;
		//System.out.println(Gdx.input.getDeltaY());
		if (-Gdx.input.getDeltaY() < 0) {
			modifier = -1;
		}
		return modifier;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
            Engine.mouseMoved(screenX, screenY);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
        Engine.scrolled(amount);
        environment.camController.scrolled(amount);
		return true;
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	public static boolean simulate() {
		return simulate;
	}

	public static void setSimulate(boolean b) {
		for(GameObject o: Engine.getWorld().instances)
		{
			if(o.haveComponent(Rigidbody.class))
			{
				(o.getComponent(Rigidbody.class)).setWorldTransform(o.getTransform());
				o.moveTransformToPRS(o.getTransform());
			}
		}
		simulate = b;
        Engine.setDoPhysics(b);
	}

	public static void openLoadDialog() {
		JFileChooser load = new JFileChooser();
		int ret = load.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			EditorScreen.LoadModel(load.getSelectedFile());
		}
	}

	public static void openLoadSceneDialog() {
		JFileChooser load = new JFileChooser();
		int ret = load.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			EditorScreen.LoadScene(load.getSelectedFile());
			EditorScreen.DeactivatePropsWindow();
			EditorScreen.environment.selected = null;
		}
	}

	public static void openLoadTextureDialog() {
		JFileChooser load = new JFileChooser();
		int ret = load.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			EditorScreen.LoadTexture(load.getSelectedFile());
		}
	}

	public static void openSaveSceneDialog() {
		JFileChooser save = new JFileChooser();

		int ret = save.showOpenDialog(null);
		if (ret == JFileChooser.APPROVE_OPTION) {
			EditorScreen.SaveScene(save.getSelectedFile());
		}
	}

    //TODO: implement saving
    public static void packResources() {

    }
}
