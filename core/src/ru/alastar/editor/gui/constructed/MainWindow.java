package ru.alastar.editor.gui.constructed;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ru.alastar.Engine;
import ru.alastar.editor.EditorScreen;
import ru.alastar.game.components.*;
import ru.alastar.gui.ConstructedGUI;
import ru.alastar.gui.GUICore;
import ru.alastar.gui.elements.MenuElement;
import ru.alastar.gui.elements.TopMenu;
import ru.alastar.lang.LanguageManager;
import ru.alastar.utils.GDebugger;

public class MainWindow extends ConstructedGUI {

	private TopMenu menu;

	public MainWindow()
	{
		menu = new TopMenu("", GUICore.getSelectedSkin(), "default");
		final MenuElement fileEl= new MenuElement(LanguageManager.getLocalizedMessage("File") ,GUICore.getSelectedSkin(), menu);
        fileEl.setName("File");
        menu.addElement(fileEl);

		fileEl.setRootElement();

        MenuElement GameObjectsEl= new MenuElement(LanguageManager.getLocalizedMessage("Object"), GUICore.getSelectedSkin(), menu);
        GameObjectsEl.setName("Object");
		GameObjectsEl.setRootElement();

		MenuElement Physics = new MenuElement(LanguageManager.getLocalizedMessage("SceneI"), GUICore.getSelectedSkin(), menu);
        menu.addElement(Physics);
        Physics.setName("Scene");
		Physics.setRootElement();

        MenuElement Options = new MenuElement(LanguageManager.getLocalizedMessage("Options"), GUICore.getSelectedSkin(), menu);
        menu.addElement(Options);
        Options.setName("Options");

		final MenuElement Selected = new MenuElement(LanguageManager.getLocalizedMessage("GO"), GUICore.getSelectedSkin(), menu);
        menu.addElement(Selected);
        Selected.setName("GO");
        Selected.setRootElement();
         MenuElement rot = null;
         MenuElement scl = null;
         final MenuElement pos = new MenuElement("P", GUICore.getSelectedSkin(), menu);
        pos.removeListener(pos.listener);
        final MenuElement finalRot = rot;
        final MenuElement finalScl = scl;
        pos.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                  EditorScreen.environment.transformation = 0;
            }

        });
        pos.setName("Pos");
        pos.setButton();

        rot = new MenuElement("R", GUICore.getSelectedSkin(), menu);
        rot.removeListener(rot.listener);
        rot.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.environment.transformation = 1;
            }

        });
        rot.setName("Rot");
        rot.setButton();

         scl = new MenuElement("S", GUICore.getSelectedSkin(), menu);
        scl.removeListener(scl.listener);
        scl.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.environment.transformation = 2;
            }

        });
        scl.setName("Scl");
        scl.setButton();

        final GDebugger gdbg = new GDebugger() {
            @Override
            public String process() {
                return "Running...";
            }
        };


        final MenuElement sim = new MenuElement("Sim", GUICore.getSelectedSkin(), menu);
        sim.removeListener(sim.listener);

        sim.addListener(new ChangeListener() {

            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.setSimulate(!EditorScreen.simulate());
                if(EditorScreen.simulate) {
                    EditorScreen.environment.xGO.setActive(false);
                    EditorScreen.environment.yGO.setActive(false);
                    EditorScreen.environment.zGO.setActive(false);
                    EditorScreen.environment.zeroGO.setActive(false);

                    Engine.addDebugger(gdbg);
                    GUICore.getByName("PropsWindow").hide();
                }
                else {
                    Engine.removeDebugger(gdbg);
                    EditorScreen.environment.xGO.setActive(true);
                    EditorScreen.environment.yGO.setActive(true);
                    EditorScreen.environment.zGO.setActive(true);
                    EditorScreen.environment.zeroGO.setActive(true);

                    if(EditorScreen.environment.selected != null)
                        EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);
                }
            }

        });
        sim.setName("Simulate");
        sim.setButton();

        menu.addElement(pos);
        menu.addElement(rot);
        menu.addElement(scl);
        menu.addElement(sim);

        menu.setWidth((float) Gdx.graphics.getWidth());

        menu.disable("GO");
		final MenuElement addComponent = Selected.createDropdown(LanguageManager.getLocalizedMessage("AddComponent"), "AddComponent", null);

		addComponent.setSection(Selected);

		addComponent.createDropdown("Rigidbody", "addRigidbody", new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.environment.selected.addComponent(new Rigidbody3D(EditorScreen.environment.selected));
				EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);

			}
		}).setButton();
		addComponent.createDropdown("Sound Emitter","addSoundEmitter",  new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.environment.selected.addComponent(new SoundEmitter());
				EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);

			}
		}).setButton();
		addComponent.createDropdown("Sound Listener","addSoundListener",  new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.environment.selected.addComponent(new SoundListener());
				EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);

			}
		}).setButton();
		addComponent.createDropdown("Animator","addAnimator",  new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.environment.selected.addComponent(new Animator());
				EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);

			}
		}).setButton();
		addComponent.createDropdown("Particle Emitter","addParticleEmitter",  new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.environment.selected.addComponent(new ParticleEmitter());
				EditorScreen.ActivatePropsWindow(EditorScreen.environment.selected);

			}
		}).setButton();


		Options.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GUICore.getByName("Options").show();
			}
		});

		/*final MenuElement simulate = Physics.createDropdown(LanguageManager.getLocalizedMessage("SwitchPhysics"), "SwitchPhysics", new ChangeListener() {

			public void changed(ChangeEvent event, Actor actor) {
				EditorScreen.setSimulate(!EditorScreen.simulate());
			}

		});*/

		final MenuElement loadModel = fileEl.createDropdown(LanguageManager.getLocalizedMessage("LoadModel"), "LoadModel",new ChangeListener() {

			public void changed(ChangeEvent event, Actor actor) {
				// System.out.println("LOAD MODEL");
				EditorScreen.openLoadDialog();
			}

		});
		loadModel.setButton();
		MenuElement loadScene = fileEl.createDropdown(LanguageManager.getLocalizedMessage("LoadScene"), "LoadScene",new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//System.out.println("Load Scene");
				EditorScreen.openLoadSceneDialog();
			}

		});
		loadScene.setButton();
		MenuElement loadTexture = fileEl.createDropdown(LanguageManager.getLocalizedMessage("LoadTexture"), "LoadTexture",new ChangeListener() {

			public void changed(ChangeEvent event, Actor actor) {
				// System.out.println("LOAD MODEL");
				EditorScreen.openLoadTextureDialog();
			}

		});
		loadTexture.setButton();
		MenuElement saveScene = fileEl.createDropdown(LanguageManager.getLocalizedMessage("SaveScene"), "SaveScene", new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//System.out.println("SAVE SCENE");
				EditorScreen.openSaveSceneDialog();
			}

		});
		saveScene.setButton();
        /*
        MenuElement saveRes = fileEl.createDropdown(LanguageManager.getLocalizedMessage("PackRes"), "PackRes", new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorScreen.packResources();
            }

        });
        saveRes.setButton();*/
		MenuElement createTerrain = GameObjectsEl.createDropdown(LanguageManager.getLocalizedMessage("Terrain"), "CreateTerrain", new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				GUICore.getByName("CreateTerrain").show();
			}
		});
		createTerrain.setButton();
	}
	
	@Override
	public Actor getByName(String s){
		if(s.equals("window"))
			return menu;
		return null;
	}
	
	@Override
	public void register(Stage s)
	{
		menu.register(s);
	}

	@Override
	public void show() {
		this.menu.setVisible(true);
		
	}

	@Override
	public void hide() {
		this.menu.setVisible(false);
	}

}
