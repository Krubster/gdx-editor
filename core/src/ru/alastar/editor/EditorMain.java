package ru.alastar.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class EditorMain extends Game implements ApplicationListener {


	/*
Changelog:

    Abandoned

    15.06.15:
     - Added transformation switching
     - Some cosmetic changes

    14.05.15:
     - Migrated to engine
     - Shadows on terrain looks very weird

    07.05.15:
     - Migrated to libgdx 1.6.0
     - Font was fixed by libgdx! :)

	03.05.15:
	 - Added component saving
	 - Remove dynamic localization
	 - Fixed top menu
	 - Checked physics

Editor status: semi-table
Editor TODO:
= Simulating button - done!
= Transformation switching - done!
= Migrate to engine finally - done!
= Material editor/saver/loader - do I need it?
  +Editing
  +Loading
  +Saving
= Shaders editor/saver/loader (???)
  +Editing
  +Loading
  +Saving
= Prefabs
  +Building
  +Referencing
  +Saving
  +Loading
= Saving scenes - must rewrite, a lot of changes
= Animator - WIP
= Particle Emitter - WIP
= Sound emitter - WIP
= Sound listener - WIP
= Physics - done!
  +Terrain collision - done!
  +Switcher - done!
  +Shapes - done! Need to add more shapes
= Fancy UI
= Options window - WIP
= Implement component parser for Game Objects - done! UI fixed
= Custom editors for components - done!
  +Editor - done!
  +Adding components - done!

	*/
	@Override
	public void create() {

		Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
				Gdx.graphics.getDesktopDisplayMode().height, false);
		Gdx.graphics.setTitle("Editor");
		this.setScreen(new EditorScreen());
	}

}
