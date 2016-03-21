package vn.dise.nytarticlesearch.activities;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import vn.dise.nytarticlesearch.models.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vn.dise.nytarticlesearch.R;
import vn.dise.nytarticlesearch.utils.DatePickerFragment;


public class SettingActivity extends DialogFragment {

    private Spinner spinner;
    private Button btnSave;
    private SettingDialogListener activity;
    private DatePicker datePicker;
    private TextView edDate;
    private Spinner spSort;
    private CheckBox chkArt;
    private CheckBox chkFashion;
    private CheckBox chkSport;


    public SettingActivity() {
    }

    public static SettingActivity newInstance(String title) {
        SettingActivity frag = new SettingActivity();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    public void getControls(View v) {
        spinner = (Spinner) v.findViewById(R.id.spSortValue);
        edDate = (TextView) v.findViewById(R.id.etBeginDate);
        spSort = (Spinner) v.findViewById(R.id.spSortValue);
        chkArt = (CheckBox) v.findViewById(R.id.chkArt);
        chkSport = (CheckBox) v.findViewById(R.id.chkSport);
        chkFashion = (CheckBox) v.findViewById(R.id.chkFashion);

        //set default values
        Date now = new Date();
        SimpleDateFormat fdate = new SimpleDateFormat("yyyy/MM/dd");
        edDate.setText(fdate.format(now));
        //set default value is Newest
        spinner.setSelection(0);
    }

    public interface SettingDialogListener {
        void onFinishSettingDialog(Filter f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_setting, container);
        getControls(view);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_values, R.layout.spinner_item);
        spinner.setAdapter(adapter);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Filter.NEWS_DESK> desksList = new ArrayList<Filter.NEWS_DESK>();
                if (chkArt.isChecked())
                    desksList.add(Filter.NEWS_DESK.ARTS);
                if (chkFashion.isChecked())
                    desksList.add(Filter.NEWS_DESK.FASHION);
                if (chkSport.isChecked())
                    desksList.add(Filter.NEWS_DESK.SPORT);

                Filter filter = new Filter(edDate.getText().toString(), spSort.getSelectedItem().toString(), desksList);
                String sort = spSort.getSelectedItem().toString();
                activity = (SettingDialogListener) getActivity();
                activity.onFinishSettingDialog(filter);
                dismiss();
            }
        });

        //Show datetime picker when user click on date EditText
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();

            }
        });
        return view;
    }

    //get data callback from DatePickerFragment
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            edDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
        }
    };

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Setting");
        getDialog().setTitle(title);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


}
