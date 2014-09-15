package com.nickan.testapp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	private static final int SPLASH = 0;
	private static final int USER_PROFILE = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS + 1;
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	private boolean isResumed = false;
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	
	private MenuItem settings;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		FragmentManager fm = getSupportFragmentManager();
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[USER_PROFILE] = fm.findFragmentById(R.id.userProfileFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int index = 0; index < fragments.length; ++index) {
			transaction.hide(fragments[index]);
		}
		transaction.commit();
		
		
		// Add code to print out the key hash
		getHashKey();
	}
	
	private void getHashKey() {
		try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.facebook.samples.hellofacebook", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	}
	
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int index = 0; index < fragments.length; ++index) {
			if (index == fragmentIndex) {
				transaction.show(fragments[index]);
			} else {
				transaction.hide(fragments[index]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager fm = getSupportFragmentManager();
			// Get the number of the entries in the back stack
			int backStackSize = fm.getBackStackEntryCount();
			// Clear the back stack
			for (int index = 0; index < backStackSize; ++index) {
				fm.popBackStack();
			}
			
			if (state.isOpened()) {
				// If the session state is open, show the authenticated fragment
				showFragment(USER_PROFILE, false);
			} else if (state.isClosed()){
				showFragment(SPLASH, false);
			}
		}
	}
	
	
	@Override
	public void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();
		
		if (session != null && session.isOpened()) {
			showFragment(USER_PROFILE, false);
		} else {
			showFragment(SPLASH, false);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	    // only add the menu when the USER_PROFILE fragment is showing
	    if (fragments[USER_PROFILE].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    return false;
	}
	
	
}	

