package ie.nci.bshbise3.prj.andengine.pinball;

import android.content.Context;

@SuppressWarnings("unused") 
enum GameMenuEnum {
/*
 * encapsulate menu details in enumerator and avail of strings.xml for localization
 * 
 */
	MENU_PLAY(1,0,R.string.menu_play_caption,"menu_play.png"),
	MENU_OPTIONS(2,0,R.string.menu_options_caption,"menu_options.png"),
	MENU_ABOUT(3,0,R.string.menu_about_caption,"menu_about.png"),
	MENU_QUIT(4,0,R.string.menu_quit_caption,"menu_quit.png");

	private final int mId;
	private final int mParentId;
	private final int mTextResourceId;
	private final String mImageFileName;

	GameMenuEnum(int id, int parentId, int textResourceId, String imageFileName){
		this.mId = id;
		this.mParentId = parentId;
		this.mTextResourceId = textResourceId;
		this.mImageFileName = imageFileName;				
	}
	
	public int id() { return this.mId; }
	public int parentId() { return this.mParentId; }
	public String text(Context c) { return c.getString(mTextResourceId); }
	public String imageFileName() { return this.mImageFileName; }
}