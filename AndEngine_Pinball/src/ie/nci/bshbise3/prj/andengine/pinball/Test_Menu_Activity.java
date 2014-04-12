package ie.nci.bshbise3.prj.andengine.pinball;

import org.andengine.AndEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.opengl.GLES20;
import android.view.KeyEvent;

public class Test_Menu_Activity extends SimpleBaseGameActivity implements
		IOnMenuItemClickListener {

	// ### MENU CONSTANTS ### //
	protected static final int 	MENU_RESET 	= 0; 
	protected static final int 	MENU_QUIT 	= 1; 

	// ### SCREEN CONSTANTS ### //
	// FIXME how to get active screen dimension before displaying a view / get a context?
	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;

	// ### CAMERA ### //
	protected Camera mCamera;

	// ### MAIN SCENE ### //
	protected Scene mMainScene;

	// ### MENU SCENE ### //
	protected MenuScene mMenuScene;
	private BitmapTextureAtlas mMenuTexture;
	protected ITextureRegion mMenuResetTextureRegion;
	protected ITextureRegion mMenuQuitTextureRegion;


	// ### CONSTRUCTOR ### //
	public Test_Menu_Activity() throws RuntimeException {
		// check if AndEngine is supported on this device
		if (!AndEngine.isDeviceSupported()) {
			AlertDialog d = new AlertDialog.Builder(this)
			.setTitle(R.string.dlg_unsupported_device_title)
			.setMessage(R.string.dlg_unsupported_device_message)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(android.R.string.ok, null).create();
			d.show();
			throw new RuntimeException("Device not supported");
		} 
	}

	// ### OVERRIDES ### //
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				this.mCamera);
	}

	@Override
	public void onCreateResources() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		// menu scene
		this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(),256, 128, TextureOptions.BILINEAR);
		this.mMenuResetTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mMenuTexture, this, "menu_reset.png", 0,0);
		this.mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mMenuTexture, this, "menu_quit.png", 0,50);
		this.mMenuTexture.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.createMenuScene();

		// create basic scene
		this.mMainScene = new Scene();
		this.mMainScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));

		// attach the menu
		this.mMainScene.setChildScene(this.mMenuScene, false, true, true);

		/*
		final Sprite face = new Sprite(0, 0, this.mFaceTextureRegion,
				this.getVertexBufferObjectManager());
		face.registerEntityModifier(new MoveModifier(30, 0, CAMERA_WIDTH
				- face.getWidth(), 0, CAMERA_HEIGHT - face.getHeight()));
		this.mMainScene.attachChild(face);
		*/

		return this.mMainScene;
	}

	// ### KEYPRESS ### //
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mMainScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				this.mMenuScene.back();
			} else {
				/* Attach the menu. */
				this.mMainScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}

	@Override
	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_RESET:
				/* Restart the animation. */
				this.mMainScene.reset();

				/* Remove the menu and reset it. */
				this.mMainScene.clearChildScene();
				this.mMenuScene.reset();
				return true;
			case MENU_QUIT:
				/* End Activity. */
				this.finish();
				return true;
			default:
				return false;
		}
	}
	
	// ### METHODS ### //
	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.mCamera);
		
		final SpriteMenuItem resetMenuItem = new SpriteMenuItem(
				MENU_RESET,
				this.mMenuResetTextureRegion,
				this.getVertexBufferObjectManager());
		resetMenuItem.setBlendFunction(
				GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(resetMenuItem);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(
				MENU_QUIT,
				this.mMenuQuitTextureRegion,
				this.getVertexBufferObjectManager());
		quitMenuItem.setBlendFunction(
				GLES20.GL_SRC_ALPHA,
				GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(quitMenuItem);

		// animate
		this.mMenuScene.buildAnimations();

		// freeze
		this.mMenuScene.setBackgroundEnabled(false);

		// add click listener
		this.mMenuScene.setOnMenuItemClickListener(this);
	}

}
