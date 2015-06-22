/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package ru.alastar.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraController extends GestureDetector {
	/** The angle to rotate when moved the full width or height of the screen. */
	private final float rotateAngle = 360f;
	/** The units to translate the camera when moved the full width or height of the screen. */
	private final float translateUnits = 10f; // FIXME auto calculate this based on the target
	/** The key which must be pressed to activate rotate, translate and forward or 0 to always activate. */
	private final int activateKey = 0;
	/** Indicates if the activateKey is currently being pressed. */
	private boolean activatePressed;
	/** Whether to update the camera after it has been changed. */
	private final boolean autoUpdate = true;
	/** The target to rotate around. */
	private Vector3 target = new Vector3();
	/** Whether to update the target on forward */
	private final boolean forwardTarget = true;/*
	private final int forwardKey = Keys.W;
	private boolean forwardPressed;
	private final int backwardKey = Keys.S;
	private boolean backwardPressed;
	private final int rotateRightKey = Keys.A;
	private boolean rotateRightPressed;
	private final int rotateLeftKey = Keys.D;
	private boolean rotateLeftPressed;*/
	/** The camera. */
	private final Camera camera;
	/** The current (first) button being pressed. */
	private int button = -1;

	private float startX, startY;
	private final Vector3 tmpV1 = new Vector3();
	private final Vector3 tmpV2 = new Vector3();

	static class CameraGestureListener extends GestureAdapter {
		public CameraController controller;
		private float previousZoom;

		@Override
		public boolean touchDown (float x, float y, int pointer, int button) {
			previousZoom = 0;
			return false;
		}

		@Override
		public boolean tap (float x, float y, int count, int button) {
			return false;
		}

		@Override
		public boolean longPress (float x, float y) {
			return false;
		}

		@Override
		public boolean fling (float velocityX, float velocityY, int button) {
			return false;
		}

		@Override
		public boolean pan (float x, float y, float deltaX, float deltaY) {
			return false;
		}

		@Override
		public boolean zoom (float initialDistance, float distance) {
			float newZoom = distance - initialDistance;
			float amount = newZoom - previousZoom;
			previousZoom = newZoom;
			float w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
			return controller.pinchZoom(amount / ((w > h) ? h : w));
		}

		@Override
		public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			return false;
		}
	}

	private CameraController(final CameraGestureListener gestureListener, final Camera camera) {
		super(gestureListener);
		CameraGestureListener gestureListener1 = gestureListener;
		gestureListener1.controller = this;
		this.camera = camera;
	}

	public CameraController (final Camera camera) {
		this(new CameraGestureListener(), camera);
	}

	public void update () {
		/*if (rotateRightPressed || rotateLeftPressed || forwardPressed || backwardPressed) {
			updateTarget();
			final float delta = Gdx.graphics.getDeltaTime();
			if (rotateRightPressed) camera.rotate(camera.up, -delta * rotateAngle);
			if (rotateLeftPressed) camera.rotate(camera.up, delta * rotateAngle);
			if (forwardPressed) {
				camera.translate(tmpV1.set(camera.direction).scl(delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
			if (backwardPressed) {
				camera.translate(tmpV1.set(camera.direction).scl(-delta * translateUnits));
				if (forwardTarget) target.add(tmpV1);
			}
		}*/
		if (autoUpdate) camera.update();
	}

	private void updateTarget() {
		vec.set(camera.direction.x,camera.direction.y,camera.direction.z);
		vec.scl(10f);
		target = camera.position.cpy().add(vec);		
	}

	private int touched;
	private boolean multiTouch;
	private final Vector3 vec = new Vector3();

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		touched |= (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (multiTouch)
			this.button = -1;
		else if (this.button < 0 && (activateKey == 0 || activatePressed)) {
			startX = screenX;
			startY = screenY;
			this.button = button;
		}
		return super.touchDown(screenX, screenY, pointer, button) || activatePressed;
	}

	@Override
	public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		touched &= -1 ^ (1 << pointer);
		multiTouch = !MathUtils.isPowerOfTwo(touched);
		if (button == this.button) this.button = -1;
		return super.touchUp(screenX, screenY, pointer, button) || activatePressed;
	}

	boolean process(float deltaX, float deltaY, int button) {
		/* The button for translating the camera along the direction axis */
		int forwardButton = Buttons.MIDDLE;/* The button for translating the camera along the up/right plane */
		int translateButton = Buttons.RIGHT;/* The button for rotating the camera. */
		int rotateButton = Buttons.LEFT;
		if (button == rotateButton) {
			tmpV1.set(camera.direction).crs(camera.up).y = 0f;
			camera.rotateAround(target, tmpV1.nor(), deltaY * rotateAngle);
			camera.rotateAround(target, Vector3.Y, deltaX * -rotateAngle);
		} else if (button == translateButton) {
			camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * translateUnits));
			camera.translate(tmpV2.set(camera.up).scl(-deltaY * translateUnits));
			/* Whether to update the target on translation */
			boolean translateTarget = true;
			if (translateTarget) target.add(tmpV1).add(tmpV2);
		} else if (button == forwardButton) {
			camera.translate(tmpV1.set(camera.direction).scl(deltaY * translateUnits));
			if (forwardTarget) target.add(tmpV1);
		}
		if (autoUpdate) camera.update();
		updateTarget();
		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		boolean result = super.touchDragged(screenX, screenY, pointer);
		if (result || this.button < 0) return result;
		final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
		final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
		startX = screenX;
		startY = screenY;
		return process(deltaX, deltaY, button);
	}

	@Override
	public boolean scrolled (int amount) {
		/* The weight for each scrolled amount. */
		float scrollFactor = -0.1f;
		return zoom(amount * scrollFactor * translateUnits);
	}

	boolean zoom(float amount) {
		/* Whether scrolling requires the activeKey to be pressed (false) or always allow scrolling (true). */
		boolean alwaysScroll = true;
		if (!alwaysScroll && activateKey != 0 && !activatePressed) return false;
		camera.translate(tmpV1.set(camera.direction).scl(amount));
		/* Whether to update the target on scroll */
		boolean scrollTarget = false;
		if (scrollTarget) target.add(tmpV1);
		if (autoUpdate) camera.update();
		updateTarget();
		return true;
	}

	boolean pinchZoom(float amount) {
		/* World units per screen size */
		float pinchZoomFactor = 10f;
		return zoom(pinchZoomFactor * amount);
	}

	@Override
	public boolean keyDown (int keycode) {
		if (keycode == activateKey) activatePressed = true;
		/*if (keycode == forwardKey)
			forwardPressed = true;
		else if (keycode == backwardKey)
			backwardPressed = true;
		else if (keycode == rotateRightKey)
			rotateRightPressed = true;
		else if (keycode == rotateLeftKey) rotateLeftPressed = true;*/
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		if (keycode == activateKey) {
			activatePressed = false;
			button = -1;
		}/*
		if (keycode == forwardKey)
			forwardPressed = false;
		else if (keycode == backwardKey)
			backwardPressed = false;
		else if (keycode == rotateRightKey)
			rotateRightPressed = false;
		else if (keycode == rotateLeftKey) rotateLeftPressed = false;*/
		return false;
	}
}
