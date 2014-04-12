package ie.nci.bshbise3.prj.andengine.pinball;

import java.util.Observable;
import java.util.Observer;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.ui.activity.BaseGameActivity;
/*
 * TODO: check this: http://android.kul.is/2013/10/android-game-development-tutorial-part-4.html
 */
public class SceneManager implements Observer {

	// ### ENUMS & PROPERTIES ### //
	public enum AllScenes {
		SPLASH, MENU, ABOUT, GAME
	}

	// globals
	private AllScenes currentScene;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;

	// splash
	private ManagedSceneSplash mSplashMS;
	private Scene splashScene;

	// menu
	private ManagedSceneMenuL1 mMenuMS;
	private MenuScene menuScene;

	// about
	private ManagedSceneAbout mAboutMS;
	private Scene aboutScene;

	// game
	private ManagedSceneGame mGameMS;
	private Scene gameScene;

	// ### CONSTRUCTOR ### //
	public SceneManager(BaseGameActivity act, Engine eng, Camera cam) {
		this.activity = act;
		this.engine = eng;
		this.camera = cam;
		
		mSplashMS = new ManagedSceneSplash(this.activity);

		mMenuMS = new ManagedSceneMenuL1(this.activity);		
		mMenuMS.addObserver(this);
		
		mGameMS = new ManagedSceneGame(this.activity);

	}

	// ### GENERAL METHODS ### //

	// get & set current scene
	public AllScenes getCurrentScene() {
		return currentScene;
	}
	public void setCurrentScene(AllScenes currentScene) throws Exception {
		this.currentScene = currentScene;
		switch (currentScene) {

		case SPLASH:
			break;

		case MENU:
			createMenuScene();
			if(mMenuMS.createMenuScene(menuScene)) engine.setScene(menuScene);
			else throw new Exception("Scene not ready");
			break;

		case ABOUT:
			createAboutScene();
			if(mAboutMS.createScene(aboutScene)) engine.setScene(aboutScene);
			else throw new Exception("Scene not ready");
			break;

		case GAME:
			createGameScene();
			if(mGameMS.createScene(gameScene)) engine.setScene(gameScene);
			else throw new Exception("Scene not ready");
			break;

		default:
			break;

		}
	}

	// ### SPLASH RESOURCE LOADERS AND SCENE CREATORS ### //
	public void loadSplashResources() {
		mSplashMS.loadResources();
	}
	public Scene createSplashScene() {
		if(splashScene == null || !mSplashMS.hasCreatedScene()){
			splashScene = new Scene();
			mSplashMS.createScene(splashScene);
		}
		return splashScene;
	}

	// ### MENU RESOURCE LOADERS AND SCENE CREATORS ### //
	public void loadMenuResources() {
		mMenuMS.loadResources();
	}
	public Scene createMenuScene() {
		if(menuScene == null || !mMenuMS.hasCreatedScene()){
			menuScene = new MenuScene(camera);
			mMenuMS.createMenuScene(menuScene);
		}
		return menuScene;

	}
	
	// ### ABOUT RESOURCE LOADERS AND SCENE CREATORS ### //
	public void loadAboutResources() {
		mAboutMS.loadResources();
	}
	public Scene createAboutScene() {
		if(aboutScene == null || !mAboutMS.hasCreatedScene()){
			aboutScene = new Scene();
			mAboutMS.createScene(aboutScene);
		}
		return aboutScene;
	}
	
	// ### GAME RESOURCE LOADERS AND SCENE CREATORS ### //
	
	public void loadGameResources() {
		mGameMS.loadResources();
	}
	public Scene createGameScene() {
		if(gameScene == null || !mGameMS.hasCreatedScene()){
			gameScene = new Scene();
			mGameMS.createScene(gameScene);
		}
		return gameScene;
	}
	
	// ### OBSERVER OVERRIDES ### //
	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0 instanceof ManagedSceneMenuL1){
			int menuItemId;
			StringBuilder actionString = new StringBuilder()
				//.append(arg0.toString())
				//.append("::")
				.append(arg1.toString())
				.append("=>");
			try {
				menuItemId = Integer.parseInt(arg1.toString());

				// when click play
				if (menuItemId ==  GameMenuEnum.MENU_PLAY.id()) {
					actionString.append(GameMenuEnum.MENU_PLAY.text(activity));
					setCurrentScene(AllScenes.GAME);
				}
				// when click options
				else if (menuItemId == GameMenuEnum.MENU_OPTIONS.id()) {
					actionString.append(GameMenuEnum.MENU_OPTIONS.text(activity));
					setCurrentScene(AllScenes.MENU);
				}
				// when click about
				else if (menuItemId == GameMenuEnum.MENU_ABOUT.id()) {
					actionString.append(GameMenuEnum.MENU_ABOUT.text(activity));
					setCurrentScene(AllScenes.ABOUT);
				}
				// when click quit
				else if (menuItemId == GameMenuEnum.MENU_QUIT.id()) {
					actionString.append(GameMenuEnum.MENU_QUIT.text(activity));
					this.activity.finish();
				} else {
					actionString.append("!?!?");			
				}
			} catch (Exception e) {
				actionString.append(" # ERR: ").append(e.getMessage());							
			}
			activity.toastOnUIThread(actionString);

		}		
	}

}