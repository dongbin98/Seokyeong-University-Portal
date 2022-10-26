package com.dbsh.skup.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.dbsh.skup.R;
import com.dbsh.skup.adapter.HomeNoticeCardAdapter;
import com.dbsh.skup.adapter.HomeTopCardAdapter;
import com.dbsh.skup.data.NoticeData;
import com.dbsh.skup.data.UserData;
import com.dbsh.skup.databinding.HomeCenterFormBinding;
import com.dbsh.skup.viewmodels.HomeCenterViewModel;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;

public class HomeCenterFragment extends Fragment {

    private HomeCenterFormBinding binding;
    private HomeCenterViewModel viewModel;

	// this Fragment
	private Fragment HomeCenterFragment;

	// parent Fragment
	private HomeCenterContainer homeCenterContainer;

    UserData userData;

    private ViewPager2 mPager, mPager2, mPager3;
    private CircleIndicator3 mIndicator;
    private FragmentStateAdapter pagerAdapter, pagerAdapter2, pagerAdapter3;

    ArrayList<NoticeData> noticeDataList, majorNoticeDataList;
    private int noticeCount = 0;
    private int majorNoticeCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Data Binding */
        binding = DataBindingUtil.inflate(inflater, R.layout.home_center_form, container, false);
        viewModel = new HomeCenterViewModel();
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

		HomeCenterFragment = this;
		homeCenterContainer = ((HomeCenterContainer) this.getParentFragment());
        userData = ((UserData) getActivity().getApplication());

        // 자식 프래그먼트에서 부모 프래그먼트 확인을 위함
        Bundle bundle = new Bundle();
        bundle.putString("type", "center");

        noticeDataList = new ArrayList<>();
        majorNoticeDataList = new ArrayList<>();

        viewModel.getNotice();
        viewModel.getMajorNotice();

        // 공지사항 더보기
        binding.mainHomeNoticePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", "https://skuniv.ac.kr/notice");
                startActivity(intent);
            }
        });

        // 학과 공지사항 더보기
        binding.mainHomeMajorNoticePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", "https://ce.skuniv.ac.kr/ce_notice");
                startActivity(intent);
            }
        });

        // 출결
        binding.mainHomeQuickBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				homeCenterContainer.pushFragment(HomeCenterFragment, new AttendanceFragment(), bundle);
            }
        });
        // 학사일정
        binding.mainHomeQuickBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        // QR
        binding.mainHomeQuickBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
	            homeCenterContainer.pushFragment(HomeCenterFragment, new QrcodeFragment(), bundle);
			}
        });
        // Portal
        binding.mainHomeQuickBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.putExtra("url", "https://sportal.skuniv.ac.kr");
                startActivity(intent);
            }
        });

        mPager = binding.viewpager;
        pagerAdapter = new HomeTopCardAdapter(getActivity(), 3);
        mPager.setAdapter(pagerAdapter);
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(3);
//        mPager.setPadding(10, 0, 10, 0);

        mIndicator = binding.indicator;
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(3,0);

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % 3);
            }
        });

        viewModel.noticeDataLiveData.observe(getViewLifecycleOwner(), new Observer<NoticeData>() {
            @Override
            public void onChanged(NoticeData noticeData) {
                if(noticeData != null) {
                    noticeCount++;
                    noticeDataList.add(noticeData);
                }
                if (noticeCount >= 5) {
                    ArrayList<Fragment> fragments = new ArrayList<>();
                    for(int i = 0; i < 5; i++) {
//                        for(int i = 0; i < noticeDataList.size(); i++) {  // 학교 공지사항 전체 다 가져오기
                        fragments.add(HomeCenterNoticeFragment.newInstance(i, noticeDataList.get(i).getTitle(), noticeDataList.get(i).getType(), noticeDataList.get(i).getDate(), noticeDataList.get(i).getDepartment(), noticeDataList.get(i).getUrl()));
                    }
                    mPager2 = binding.viewpager2;
                    pagerAdapter2 = new HomeNoticeCardAdapter(getActivity(), fragments);
                    mPager2.setAdapter(pagerAdapter2);
                    mPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

                    mPager2.setCurrentItem(0);
                    mPager2.setOffscreenPageLimit(3);
                    mPager2.setPadding(0, 0, 90, 0);

                    mPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                            if (positionOffsetPixels == 0) {
                                mPager2.setCurrentItem(position);
                            }
                        }
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                        }
                    });
                }
            }
        });

        viewModel.majorNoticeDataLiveData.observe(getViewLifecycleOwner(), new Observer<NoticeData>() {
            @Override
            public void onChanged(NoticeData noticeData) {
                if(noticeData != null) {
                    majorNoticeCount++;
                    majorNoticeDataList.add(noticeData);
                }
                if (majorNoticeCount >= 5) {
                    ArrayList<Fragment> fragments2 = new ArrayList<>();
                    for(int i = 0; i < 5; i++) {
//                    for(int i = 0; i < majorNoticeDataList.size(); i++) { // 학과 공지사항 전체 다 가져오기
                        fragments2.add(HomeCenterNoticeFragment.newInstance(i, majorNoticeDataList.get(i).getTitle(), majorNoticeDataList.get(i).getType(), majorNoticeDataList.get(i).getDate(), majorNoticeDataList.get(i).getDepartment(), majorNoticeDataList.get(i).getUrl()));
                    }
                    mPager3 = binding.viewpager3;
                    pagerAdapter3 = new HomeNoticeCardAdapter(getActivity(), fragments2);
                    mPager3.setAdapter(pagerAdapter3);
                    mPager3.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    mPager3.setCurrentItem(0);
                    mPager3.setOffscreenPageLimit(3);
                    mPager3.setPadding(0, 0, 90, 0);

                    mPager3.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                            if (positionOffsetPixels == 0) {
                                mPager3.setCurrentItem(position);
                            }
                        }
                        @Override
                        public void onPageSelected(int position) {
                            super.onPageSelected(position);
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }
}