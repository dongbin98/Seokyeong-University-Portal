package com.dbsh.skup.tmpstructure.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dbsh.skup.R;
import com.dbsh.skup.attendance.AttendanceActivity;
import com.dbsh.skup.databinding.HomeTmpLeftFormBinding;
import com.dbsh.skup.graduate.GraduateActivity;
import com.dbsh.skup.qrcode.QrcodeActivity;
import com.dbsh.skup.scholarship.ScholarshipActivity;
import com.dbsh.skup.tmpstructure.viewmodels.HomeLeftViewModel;
import com.dbsh.skup.tuition.TuitionActivity;

public class HomeLeftFragment extends Fragment {

    private HomeTmpLeftFormBinding binding;
    private HomeLeftViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* Data Binding */
        binding = DataBindingUtil.inflate(inflater, R.layout.home_tmp_left_form, container, false);
        viewModel = new HomeLeftViewModel();
        binding.setViewModel(viewModel);
        binding.executePendingBindings();


		// 출결 버튼
        binding.mainMenuAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                startActivity(intent);
            }
        });

		// 시간표 버튼
        binding.mainMenuTimetableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), TimetableActivity.class);
//                startActivity(intent);
            }
        });

        // 성적 버튼
        binding.mainMenuGradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), GradeActivity.class);
//                startActivity(intent);
            }
        });

        // 강의 버튼
        binding.mainMenuLectureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), LecturePlanActivity.class);
//                startActivity(intent);
            }
        });

        // 장학 버튼
        binding.mainMenuScholarshipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScholarshipActivity.class);
                startActivity(intent);
            }
        });

        // 등록 버튼
        binding.mainMenuRegistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TuitionActivity.class);
                startActivity(intent);
            }
        });

        // 졸업 버튼
        binding.mainMenuGraduateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GraduateActivity.class);
                startActivity(intent);
            }
        });

        // 비교과 버튼
        binding.mainMenuPotenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), TempPotenSelectActivity.class);
//                startActivity(intent);
            }
        });

        // QR 버튼
        binding.mainMenuQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QrcodeActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}