package ie.nci.bshbise3.prj.andengine.pinball;

import org.andengine.AndEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Default_Activity extends Activity {
// not used. see GameActivity.class
	
	// ### CONSTANTS ### //

	private final int DLG_UNSUPPORTED_DEVICE = 0;

	// ### CONSTRUCTORS ### //
	public Default_Activity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load the XML view
		this.setContentView(R.layout.default_layout);
		
		// NOT VIABLE!! HITTING BACK ON NEW ACTIVITY LOOPS BACK IN
		// === TestAndLoadAndEngine();

	}

	private void TestAndLoadAndEngine() {
		// check if AndEngine is supported on this device
		if (!AndEngine.isDeviceSupported()) {
			this.showDialog(this.DLG_UNSUPPORTED_DEVICE);
		} else {
			try {
				final Intent i = new Intent(this, GameActivity.class);
				this.startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ### SUPER Class/Interface METHODS ###//

	@Override
	protected Dialog onCreateDialog(final int pId) {
		switch (pId) {
		case DLG_UNSUPPORTED_DEVICE:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.dlg_unsupported_device_title)
					.setMessage(R.string.dlg_unsupported_device_message)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(android.R.string.ok, null).create();

		default:
			return super.onCreateDialog(pId);
		}
	}

}
