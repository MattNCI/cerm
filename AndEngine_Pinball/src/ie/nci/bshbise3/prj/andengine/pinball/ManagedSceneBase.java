package ie.nci.bshbise3.prj.andengine.pinball;

import java.util.Observable;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

public abstract class ManagedSceneBase extends Observable {

	// control
	private Boolean _activityLoaded;
	private Boolean _resourcesLoaded;
	private Boolean _sceneCreated;

	// ref context
	private BaseGameActivity anActivity;

	// constructors
	public ManagedSceneBase() {
		initialiseControl();
	}
	public ManagedSceneBase(BaseGameActivity pActivity) {
		initialiseControl();
		setActivity(pActivity);
	}
	private void initialiseControl() {
		setActivityLoaded(false);
		setResourcesLoaded(false);
		setSceneCreated(false);
	}

	// get & set
	public void setActivity(BaseGameActivity anActivity) throws IllegalArgumentException {
		if (anActivity == null) throw new IllegalArgumentException();
		this.anActivity = anActivity;
		setActivityLoaded(true);
	}
	public BaseGameActivity getActivity() {
		return anActivity;
	}
	private void setActivityLoaded(Boolean hasLoaded) {
		this._activityLoaded = hasLoaded;
	}
	public Boolean hasLoadedActivity() {
		return _activityLoaded;
	}
	private void setResourcesLoaded(Boolean hasLoaded) {
		this._resourcesLoaded = hasLoaded;
	}
	public Boolean hasLoadedResources() {
		return _resourcesLoaded;
	}
	private void setSceneCreated(Boolean isCreated) {
		_sceneCreated = isCreated;
	}
	public Boolean hasCreatedScene() {
		return _sceneCreated;
	}

	// method to be implemented (actual scene creation)
	protected abstract void onCreateScene(Scene aScene);
	protected abstract void onCreateMenuScene(MenuScene aMenuScene);

	// method to be called to create the scene
	public Boolean createScene(Scene pScene) {
		if (!hasCreatedScene() || pScene.isDisposed()) {
			try {
				// check resources
				if (!hasLoadedResources()) loadResources();

				// create the scene
				onCreateScene(pScene);
				
				setSceneCreated(true);
			} catch (final Exception e) {
				Debug.e(e);
				setSceneCreated(false);
			}

		}
		return hasCreatedScene();
	}
	public Boolean createMenuScene(MenuScene pMenuScene) {
		if (!hasCreatedScene() || pMenuScene.isDisposed()) {
			try {
				// check resources
				if (!hasLoadedResources()) loadResources();

				// create the menu scene
				onCreateMenuScene(pMenuScene);

				setSceneCreated(true);
			} catch (final Exception e) {
				Debug.e(e);
				setSceneCreated(false);
			}
		}
		return hasCreatedScene();
	}

	// method to be implemented (actual resource loading)
	protected abstract void onLoadResources();

	// method to be called to load the resources
	public Boolean loadResources() {
		try {
			// load resources
			onLoadResources();
			setResourcesLoaded(true);
		} catch (final Exception e) {
			Debug.e(e);
			setResourcesLoaded(false);
		}
		return hasLoadedResources();
	}
}
