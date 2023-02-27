package com.dbsh.skup.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dbsh.skup.R;
import com.dbsh.skup.databinding.HomeFormBinding;
import com.dbsh.skup.viewmodels.HomeViewModel;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
	Menu menu;
	HomeCenterContainer centerContainer;
	HomeLeftContainer leftContainer;
	HomeRightContainer rightContainer;

	private long time = 0;
	private HomeFormBinding binding;
	private HomeViewModel viewModel;

	public void setOnBackPressedListener(OnBackPressedListener listener) {
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		/* DataBinding */
		binding = DataBindingUtil.setContentView(this, R.layout.home_form);
		binding.setLifecycleOwner(this);
		viewModel = new HomeViewModel();
		binding.setViewModel(viewModel);
		binding.executePendingBindings();	// 바인딩 강제 즉시실행

		menu = binding.bottomNavi.getMenu();
		centerContainer = new HomeCenterContainer();
		getSupportFragmentManager().beginTransaction().add(binding.mainContainer.getId(), centerContainer).commit();
		menu.findItem(R.id.home_fragment).setChecked(true);

		binding.bottomNavi.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.menu_framgent:
						if(leftContainer == null) {
							leftContainer = new HomeLeftContainer();
							getSupportFragmentManager().beginTransaction().add(binding.mainContainer.getId(), leftContainer).commit();
						}
						if(leftContainer != null) {
							if(leftContainer.isVisible()) {
								List<Fragment> fragments = leftContainer.getChildFragmentManager().getFragments();
								for (Fragment fragment : fragments) {
									if (fragment instanceof OnBackPressedListener)
										((OnBackPressedListener) fragment).onBackPressed();
								}
							}
							getSupportFragmentManager().beginTransaction().show(leftContainer).commit();
						}
						if(centerContainer != null)
							getSupportFragmentManager().beginTransaction().hide(centerContainer).commit();
						if(rightContainer != null)
							getSupportFragmentManager().beginTransaction().hide(rightContainer).commit();
						break;
					case R.id.home_fragment:
						if(centerContainer == null) {
							centerContainer = new HomeCenterContainer();
							getSupportFragmentManager().beginTransaction().add(binding.mainContainer.getId(), centerContainer).commit();
						}
						if(leftContainer != null)
							getSupportFragmentManager().beginTransaction().hide(leftContainer).commit();
						if(centerContainer != null) {
							if(centerContainer.isVisible()) {
								List<Fragment> fragments = centerContainer.getChildFragmentManager().getFragments();
								for (Fragment fragment : fragments) {
									if (fragment instanceof OnBackPressedListener)
										((OnBackPressedListener) fragment).onBackPressed();
								}
							}
							getSupportFragmentManager().beginTransaction().show(centerContainer).commit();
						}
						if(rightContainer != null)
							getSupportFragmentManager().beginTransaction().hide(rightContainer).commit();
						break;
					case R.id.setting_fragment:
						if(rightContainer == null) {
							rightContainer = new HomeRightContainer();
							getSupportFragmentManager().beginTransaction().add(binding.mainContainer.getId(), rightContainer).commit();
						}
						if(leftContainer != null)
							getSupportFragmentManager().beginTransaction().hide(leftContainer).commit();
						if(centerContainer != null)
							getSupportFragmentManager().beginTransaction().hide(centerContainer).commit();
						if(rightContainer != null) {
							if(rightContainer.isVisible()) {
								List<Fragment> fragments = rightContainer.getChildFragmentManager().getFragments();
								for (Fragment fragment : fragments) {
									if (fragment instanceof OnBackPressedListener)
										((OnBackPressedListener) fragment).onBackPressed();
								}
							}
							getSupportFragmentManager().beginTransaction().show(rightContainer).commit();
						}
						break;
				}
				return true;
			}
		});
	}

	@Override
	public void onBackPressed() {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		for(Fragment fragment : fragments) {
			if(fragment instanceof OnBackPressedListener && fragment.isVisible()) {
				((OnBackPressedListener)fragment).onBackPressed();
				return;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
