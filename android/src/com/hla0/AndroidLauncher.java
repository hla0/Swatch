package com.hla0;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.hla0.Swatch;

public class AndroidLauncher extends AndroidApplication {
	private static final String TAG = "Launcher";
	protected AdView adView;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//RelativeLayout layout = new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//View gameView = initializeForView(new Swatch(),config);
		//layout.addView(gameView);
		initialize(new Swatch(), config);
		//setupAds();
		//RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		//adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		//layout.addView(adView,adParams);

		//setContentView(layout);

	}
	public void setupAds() {
		adView = new AdView(this);
		adView.setVisibility(View.VISIBLE);
		adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
		adView.setAdSize(AdSize.BANNER.SMART_BANNER);
	}
}
