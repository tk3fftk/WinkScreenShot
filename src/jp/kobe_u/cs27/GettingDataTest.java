package jp.kobe_u.cs27;

import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.GazeManager.ApiVersion;
import com.theeyetribe.client.GazeManager.ClientMode;

/**
 *
 * @author tktk
 *
 */
public class GettingDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize
		final GazeManager gm = GazeManager.getInstance();
		boolean success = gm.activate(ApiVersion.VERSION_1_0, ClientMode.PUSH);

		final GazeListener gazeListener = new GazeListener();
		gm.addGazeListener(gazeListener);

		// TODO : Do awesome gaze controle wizardry

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				gm.removeGazeListener(gazeListener);
				gm.deactivate();
			}
		});

	}

}
