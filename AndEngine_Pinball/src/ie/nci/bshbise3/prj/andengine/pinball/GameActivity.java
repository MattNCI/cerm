package ie.nci.bshbise3.prj.andengine.pinball;

import ie.nci.bshbise3.prj.andengine.pinball.SceneManager.AllScenes;

import org.andengine.AndEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

public class GameActivity extends BaseGameActivity {

	private final int DLG_UNSUPPORTED_DEVICE = 0;

	Scene scene;
	protected static final int CAMERA_WIDTH = 480;
	protected static final int CAMERA_HEIGHT = 800;
	protected static final float SPLASH_SECONDS = 5;

	// new stuff here
	SceneManager sceneManager;
	Camera mCamera;

	public GameActivity() {}
	
	@Override
	public void onCreate(final Bundle savedInstanceState) throws RuntimeException {
		super.onCreate(savedInstanceState);

		// tell the user we're up!
		if(savedInstanceState == null){
			this.toastOnUIThread(this.getString(R.string.tst_app_start_welcome));
	
			// check if AndEngine is supported on this device
			if (!AndEngine.isDeviceSupported()) {
				this.showDialog(this.DLG_UNSUPPORTED_DEVICE);
				throw new RuntimeException(this.getString(R.string.err_unsupported_device_message));
			}
		} else {
			this.toastOnUIThread(this.getString(R.string.tst_app_resume_welcome));			
		}

	}


	@Override
	public EngineOptions onCreateEngineOptions() {

		// modification here:
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions options = new EngineOptions(
				true,
				ScreenOrientation.PORTRAIT_FIXED, 
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				mCamera
		);
		return options;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
		// load only the splash screen for now
		// give feedback we are working on something while the rest of the loading process runs
		// note: mEngine is defined in the super class (BaseGameActivity)
		sceneManager = new SceneManager(this, mEngine, mCamera);
		sceneManager.loadSplashResources();
		
		// let the game engine know we are done loading the resources we need.
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
		//get going with other resources
		/*
		sceneManager.loadMenuResources();
		sceneManager.loadAboutResources();
		sceneManager.loadGameResources();
		*/
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
		// all the huffing and puffing is done within the sceneManager
		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());

		//get going with other scenes
		/*
		sceneManager.createMenuScene();
		sceneManager.createAboutScene();
		sceneManager.createGameScene();
		*/
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {

		mEngine.registerUpdateHandler(new TimerHandler(
				SPLASH_SECONDS,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						loadMenu();
					}
				}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	private void loadMenu(){
		sceneManager.loadMenuResources();
		sceneManager.createMenuScene();
		try {
			sceneManager.setCurrentScene(AllScenes.MENU);
		} catch (Exception e) {
			Debug.e(e);
		}		
	}
	private void loadAbout(){
		sceneManager.loadAboutResources();
		sceneManager.createAboutScene();
		try {
			sceneManager.setCurrentScene(AllScenes.ABOUT);
		} catch (Exception e) {
			Debug.e(e);
		}		
	}
	private void loadGame(){
		sceneManager.loadGameResources();
		sceneManager.createGameScene();
		try {
			sceneManager.setCurrentScene(AllScenes.GAME);
		} catch (Exception e) {
			Debug.e(e);
		}		
	}
	
	// ### DIALOGS ### //
	@Override
	protected Dialog onCreateDialog(final int pId) {
		switch (pId) {
		
			// tell the user we cannot run on this device
			case DLG_UNSUPPORTED_DEVICE:
				return new AlertDialog.Builder(this)
						.setTitle(R.string.dlg_unsupported_device_title)
						.setMessage(R.string.dlg_unsupported_device_message)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.ok, null).create();
			
			// nothing for us, pass-on and bubbbble up.
			default:
				return super.onCreateDialog(pId);
		}
	}
	
}