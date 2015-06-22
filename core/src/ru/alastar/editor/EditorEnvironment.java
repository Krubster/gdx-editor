package ru.alastar.editor;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.JsonReader;
import ru.alastar.Engine;
import ru.alastar.editor.brushes.*;
import ru.alastar.game.ChunkModel;
import ru.alastar.game.GameObject;
import ru.alastar.game.Particle;
import ru.alastar.game.Terrain;
import ru.alastar.game.components.MeshRenderer;
import ru.alastar.graphics.GDirectionalLight;
import ru.alastar.gui.GUICore;
import ru.alastar.utils.ObjectPooler;

import java.util.Hashtable;

public class EditorEnvironment {

	public final float radius;

	public int brushRadius = 7;
	public int brushStrength = 1;

	private final Model model;
	public final GameObject zeroGO;
	public final GameObject zGO;
	public final GameObject xGO;
	public final GameObject yGO;

	@SuppressWarnings("rawtypes")
	public final ModelLoader modelLoader;

	public int visible = 0;

	final Hashtable<String, Model> models;
	private Hashtable<BrushType, Brush> brushes;

	public GameObject selected = null;

	public final Hashtable<String, Texture> textures;
	float mod = 1;
	private GameObject go;
	private final Vector3 intersectPoint = new Vector3();
	boolean scheduling = false;
	public final static int vertexSize = 9;
	public BrushMode brushMode = BrushMode.Edit;
	private Brush activeBrush = new BrushCircle();
	private long lastEdit = 0;
	private Pixmap brushTexture = (new Pixmap(1,1, Pixmap.Format.RGBA8888));
	private final BoundingBox tmpBB = new BoundingBox();
	private final Vector3 tmpV = new Vector3();
	private final float[] vertices= new float[9801];
	private static final Rectangle tmpR1 = new Rectangle();
	private static final Rectangle tmpR2 = new Rectangle();
	private Mesh m;
	private int j;
	private int i;
	private final float[] changed = new float[1];
	private final Vector3 vec = new Vector3();
	private long intencity = 95;
	private float dst;
    public CameraController camController;
    public Camera camera;
    private ModelBatch modelBatch;
    private SpriteBatch batch;
    public int transformation = 0; // 0 - position, 1- rotation, 2 - scale

    public void dispose()
	{
		model.dispose();
		brushTexture.dispose();
		if(m != null)
		m.dispose();
	}
	
	public EditorEnvironment() {
		JsonReader reader = new JsonReader();
		modelLoader = new G3dModelLoader(reader);
		models = new Hashtable<String, Model>();
		textures = new Hashtable<String, Texture>();
        camera = new ru.alastar.game.components.GCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);


		ModelBuilder modelBuilder = new ModelBuilder();

		model = modelBuilder.createBox(0.7f, 0.7f, 0.7f, new Material(
				ColorAttribute.createDiffuse(Color.BLACK)), VertexAttributes.Usage.Position
				| VertexAttributes.Usage.Normal);

        ModelInstance instance = new ModelInstance(model);
        zeroGO = new GameObject(instance, "0", "");
        zeroGO.setSerializeIgnore(true);
		Material xOriginalMaterial = new Material();
		xOriginalMaterial.set(ColorAttribute.createDiffuse(Color.GREEN));

		Material yOriginalMaterial = new Material();
		yOriginalMaterial.set(ColorAttribute.createDiffuse(Color.BLUE));

		Material zOriginalMaterial = new Material();
		zOriginalMaterial.set(ColorAttribute.createDiffuse(Color.RED));

		ModelInstance zInstance = new ModelInstance(model);

		zInstance.materials.get(0).set(zOriginalMaterial);

        zGO = new GameObject(zInstance, "z", "");
        zGO.setSerializeIgnore(true);

        ModelInstance		xInstance = new ModelInstance(model);
		xInstance.materials.get(0).set(xOriginalMaterial);

        xGO = new GameObject(xInstance, "x", "");
        xGO.setSerializeIgnore(true);

        ModelInstance yInstance = new ModelInstance(model);
		yInstance.materials.get(0).set(yOriginalMaterial);

        yGO = new GameObject(yInstance, "y", "");
        yGO.setSerializeIgnore(true);

        instance.transform.set(Vector3.Zero, new Quaternion(0, 0, 0, 0));

        modelBatch = new ModelBatch(new DefaultShaderProvider());
        batch = new SpriteBatch();
        camController = new CameraController(camera);
        Engine.registerInputProcessor(camController);
        Engine.getEnvironment().set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        Engine.getEnvironment().add(new GDirectionalLight(1024, 1024, 30f, 30f, 1f, 100f, new Vector3(15,10,5), new Vector3(0,-1,0)).set(0.8f, 0.8f, 0.8f));
		setupBrushes();
		lastEdit = System.currentTimeMillis();
		BoundingBox bounds = new BoundingBox();
		xInstance.calculateBoundingBox(bounds);
		Vector3 dimensions = new Vector3();
		bounds.getDimensions(dimensions);
		radius = dimensions.len() / 2f * mod;
		ObjectPooler.createPoolOfType(GameObject.class).extend(15);
		ObjectPooler.createPoolOfType(Particle.class).extend(100);
        zGO.setActive(false);
        xGO.setActive(false);
        yGO.setActive(false);

	}

	private void setupBrushes() {
		brushes = new Hashtable<BrushType, Brush>();
		brushes.put(BrushType.Quad, new BrushQuad());
        brushes.put(BrushType.Circle, new BrushCircle());

        setBrushType(BrushType.Quad);
	}

	public void render() {

		this.visible = 0;

		if (selected != null) {
			vec.set(0,0,0);
			EditorScreen.environment.selected.getTransform().getTranslation(vec);

			dst = Vector3.dst(camera.position.x, camera.position.y,
					camera.position.z, vec.x, vec.y, vec.z);
			EditorScreen.environment.mod = dst / Vector3.dst(0, 0, 0, 10, 10, 10);

			EditorScreen.environment.zGO.getTransform().set(vec.x, vec.y, vec.z + 2.0f * EditorScreen.environment.mod, 0, 0, 0,
                    0);
			EditorScreen.environment.xGO.getTransform().set(vec.x + 2.0f * EditorScreen.environment.mod, vec.y, vec.z, 0, 0, 0,
                    0);
			EditorScreen.environment.yGO.getTransform().set(vec.x, vec.y + 2.0f * EditorScreen.environment.mod, vec.z, 0, 0, 0,
                    0);

			EditorScreen.environment.zGO.getTransform().scl(EditorScreen.environment.mod);
			EditorScreen.environment.xGO.getTransform().scl(EditorScreen.environment.mod);
			EditorScreen.environment.yGO.getTransform().scl(EditorScreen.environment.mod);
		}

		batch.begin();
		GUICore.getFont().draw(batch,
                "Camera position: " + camera.position.toString() + " FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 66);

		batch.end();

		if (scheduling && selected != null
				&& selected.tag.equals("terrain") && System.currentTimeMillis() - lastEdit > intencity) {
			lastEdit = System.currentTimeMillis();
			for (i = 0; i < ((Terrain)go).chunks.length; ++i) {
				for (j = 0; j < ((Terrain)go).chunks.length; ++j) {

					ChunkModel n = ((Terrain) go).chunks[i][j];
					m = n.getMesh();
					if (InMesh(i * 32, intersectPoint.x, j * 32,
							intersectPoint.z, brushRadius)) {

					if (EditorScreen.environment.brushMode == BrushMode.Paint) {
							n.setMaterial(new Material(new TextureAttribute(TextureAttribute.Diffuse, new Texture(activeBrush.paint(brushRadius, brushStrength, n.texture, intersectPoint, brushTexture, i, j)))));
					}
					else {
                        m.getVertices(vertices);
                        activeBrush.deform(go, n, m, i, j, vertices, brushStrength, brushRadius, intersectPoint);
					}
				}
				}
			}
		}

	}
	private boolean InMesh(float xBegin, float pointX, float yBegin,
			float pointY, float radius) {
		return tmpR1.set(pointX - radius, pointY - radius, radius * 2, radius * 2).overlaps(tmpR2.set(xBegin, yBegin, 32, 32));
	}
	boolean intersect(Ray ray) {
		intersectPoint.set(0,0,0);
		for(i = 0; i < ((Terrain)selected).chunks.length; ++i){
			for(j = 0; j < ((Terrain)selected).chunks.length; ++j){
				ChunkModel cm = ((Terrain) selected).chunks[i][j];
				tmpBB.set(cm.getComponent(MeshRenderer.class).getBounds());
				tmpBB.mul(cm.getTransform());
				if (Intersector.intersectRayBounds(ray, tmpBB, intersectPoint)) {
					go = selected;
					//intersectPoint.mul(cm.getTransform());;
					return true;
					//return intersect(ray, cm, cm.getTransform());
				}
			}
		}
		return false;
	}

	public boolean touchDown(int screenX, int screenY) {
		if (selected != null && selected.tag.equals("terrain")) {
			if (intersect(camera.getPickRay(screenX, screenY))) {
				AddDeformation();
				return false;
			} else
				return true;
		}
		return true;
	}

	private void AddDeformation() {
		tmpV.set(0,0,0);
		selected.getPosition(tmpV);
		intersectPoint.sub(tmpV);
	//	intersectPoint.set(intersectPoint.x - tmpV.x, 0, intersectPoint.z
	//			- tmpV.z);
		 zeroGO.setPosition(intersectPoint);
		scheduling = true;
	}

	boolean inRange(double d, double centerX, double e, double f, int brushRadius2) {
		return (((centerX - brushRadius2) < d) && ((centerX + brushRadius2) > d))
				&& (((f - brushRadius2) < e) && ((f + brushRadius2) > e));
	}

	public void setBrushType(BrushType valueOf) {
		if(brushes.containsKey(valueOf))
		{
			this.activeBrush = brushes.get(valueOf);
		}
		else
		{
			this.activeBrush = new BrushQuad();
		}
	}

	public void pushBrushTexture(String selected2) {
		if(!textures.get(selected2).getTextureData().isPrepared())
		textures.get(selected2).getTextureData().prepare();
		this.brushTexture.dispose();
		this.brushTexture = textures.get(selected2).getTextureData().consumePixmap();
	}

    public void removeGameObject(GameObject go) {
        Engine.getWorld().removeGameObject(go);
    }
}
