package jp.kobe_u.cs27;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

/**
 *
 * @author tktk
 *
 */
public class GazeListener implements IGazeListener {

	// winkする秒数
	private static int winkTime = 1500;
	// スクショ保存場所
	private static String path = "";

	// 視線 左右 それぞれ
	private double gLeftX;
	private double gLeftY;
	private double gRightX;
	private double gRightY;

	// ウインクとまばたきを保持
	private boolean isWinkRight;
	private long startWinkTime;
	private boolean isBlink;

	// 時間取得用
	private Calendar cal;

	// 画面操作用
	private Robot robot;
	private Dimension screenSize;

	// コンストラクタ
	public GazeListener() {
		try {
			robot = new Robot();
			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		} catch (AWTException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public void onGazeUpdate(GazeData gazeData) {

		// 視線 左右 それぞれ
		gLeftX = gazeData.leftEye.rawCoordinates.x;
		gLeftY = gazeData.leftEye.rawCoordinates.y;
		gRightX = gazeData.rightEye.rawCoordinates.x;
		gRightY = gazeData.rightEye.rawCoordinates.y;

		/*
		 * Trackingの状態 マスクされた値なので，static変数との比較で分類． おそらくCalibration
		 * Qualityが高くないと，GAZE, EYESはとれない?
		 */
		int state = gazeData.state;

		// 右目をつむる && ウインク状態でない && 左目が開いている 場合はウインク開始
		if (gRightX == 0 && !isWinkRight && gLeftX > 0) {
			// ウインク開始時刻
			startWinkTime = gazeData.timeStamp;
			isWinkRight = true;
		} else if (gRightX > 0) {
			isWinkRight = false;
		}

		// PRESENCEより上が出ないので，とりあえずstateがPRESENCEのときのみ
		// ウインク状態でFAIL or LOST 時にウインク実行を防ぐ
		if ((gazeData.timeStamp - startWinkTime) >= winkTime && isWinkRight
				&& state == GazeData.STATE_TRACKING_PRESENCE) {
			System.out.println(String.valueOf(gazeData.timeStamp) + ", "
					+ String.valueOf(startWinkTime));
			// ウインク開始時刻を更新(連射を防ぐ)
			// startWinkTime = gazeData.timeStamp;
			isWinkRight = false;
			try {
				cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String strDate = sdf.format(cal.getTime());

				BufferedImage image = robot.createScreenCapture(new Rectangle(
						0, 0, screenSize.width, screenSize.height));
				ImageIO.write(image, "PNG", new File(path + strDate + ".png"));
				System.out.println("スクリーンショットを撮影しました。" + path + strDate
						+ ".png");
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

}
