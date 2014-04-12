package ie.nci.bshbise3.prj.andengine.pinball;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Second_Activity extends Activity {

	// ### CONSTRUCTORS ### //
	public Second_Activity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {

		// restore state (if any!?)
		super.onCreate(savedInstanceState);

		Toast.makeText(this, "Second Activity Starting up...", Toast.LENGTH_SHORT).show();

		// load the XML view
		this.setContentView(R.layout.second_layout);
		
	}
	
}
