package com.minhld.copynsee;

import com.minhld.copynsee.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		startService(new Intent(this, FloatService.class));

	}

}
