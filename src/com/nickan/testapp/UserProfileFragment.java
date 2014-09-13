package com.nickan.testapp;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserProfileFragment extends Fragment {
	private static final String TAG = "UserProfileFragment";
	
	TextView id;
	TextView name;
	TextView link;
	TextView gender;
	TextView locale;
	
	View view;
	
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onStateStatusChange(session, state, exception);
		}
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.user_profile, container, false);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		return view;
	}
	
	private void buildUserInfo(Session session) {
		id = (TextView) view.findViewById(R.id.id);
		name = (TextView) view.findViewById(R.id.name);
		link = (TextView) view.findViewById(R.id.link);
		gender = (TextView) view.findViewById(R.id.gender);
		locale = (TextView) view.findViewById(R.id.locale);
		
		if (session.isOpened()) {
			Log.e(TAG, "Session is open");
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (response.getError() != null) {
						Log.e(TAG, "Error " + response.getError().getErrorMessage());
						return;
					}
					
					if (user == null) {
						Log.e(TAG, "GraphUser is null");
						return;
					}
					
					id.setText("ID: " + user.getId());
					name.setText("Name: " + user.getName());
					link.setText("Link: " + user.getLink());
					gender.setText("Gender: " + user.asMap().get("gender").toString());
					locale.setText("Locale: " + user.getLocation());
				//	age_range.setText("Age range: " + user.asMap().get("age_range").toString());
				}
			}).executeAsync();
			
			
		} else if (session.isClosed()) {
			Log.e(TAG, "Session is closed");
		}
	}
	
	
	private void onStateStatusChange(Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			buildUserInfo(session);
		}
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
 	}
	
	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		uiHelper.onSaveInstanceState(state);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	
}
