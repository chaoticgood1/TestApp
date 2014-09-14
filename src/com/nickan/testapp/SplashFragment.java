package com.nickan.testapp;

import java.util.Arrays;

import com.facebook.widget.LoginButton;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SplashFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.splash, container, false);
		
		// Just use if there is a permission needed to be asked
		
		LoginButton logButton = (LoginButton) view.findViewById(R.id.login_button);
		logButton.setFragment(this);
	//	logButton.clearPermissions();
	//	logButton.setReadPermissions(Arrays.asList("user_about_me"));
		
		return view;
	}
	
	
	
}
