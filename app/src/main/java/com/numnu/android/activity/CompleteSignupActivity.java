package com.numnu.android.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.numnu.android.Manifest;
import com.numnu.android.R;
import com.numnu.android.fragments.ProfileFragment;
import com.numnu.android.utils.PreferencesHelper;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CompleteSignupActivity extends AppCompatActivity {
    Context context = this;
    EditText mUserName,mName,mCity,mGender,mDob,mFoodPreferences;
    Button mCompleteSignUp;
    RadioGroup mRadioGroup;
    RadioButton mRadioMale,mRadioFemale;
    private String mGenderValue = "";
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_signup);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Complete Sign Up");

        mUserName = findViewById(R.id.et_signup_user_name);
        mName = findViewById(R.id.et_signup_name);
        mCity = findViewById(R.id.et_signup_city);
//        mGender = findViewById(R.id.et_signup_gender);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioMale = findViewById(R.id.male_radio);
        mRadioFemale = findViewById(R.id.female_radio);
        mDob = findViewById(R.id.et_signup_dob);
        mFoodPreferences = findViewById(R.id.et_signup_food_preferences);
        mCompleteSignUp = findViewById(R.id.button_complete_signup);


        mDob.setInputType(InputType.TYPE_NULL);
        mDob.requestFocus();

        final RecipientEditTextView recipientEditTextView = findViewById(R.id.et_signup_food_preferences);
        recipientEditTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter recipientAdapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE,context);
        recipientAdapter.setShowMobileOnly(false);
        recipientEditTextView.setAdapter(recipientAdapter);


        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == R.id.male_radio) {
                mGenderValue = "Male";

            } else  if (checkedId == R.id.female_radio) {
                mGenderValue = "Female";
            }
        }
        });

        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog pickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(i, i1, i2);
                        mDob.setText(dateFormat.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH));
                pickerDialog.show();
            }
        });


        mCompleteSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = mUserName.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String city = mCity.getText().toString().trim();
                String gender = mGenderValue.trim();
                String dob = mDob.getText().toString().trim();
                String foodPreferences = mFoodPreferences.getText().toString();

                if ((userName.isEmpty()&&name.isEmpty()&&city.isEmpty()&&dob.isEmpty()&&foodPreferences.isEmpty()))
                {
                    Intent intent = new Intent(CompleteSignupActivity.this,HomeActivity.class);
                    intent.putExtra("completesignup","showprofilefragment");
                    startActivity(intent);

                    DrawableRecipientChip[] chips = recipientEditTextView.getSortedRecipients();

                    CompleteSignupActivity.this.finish();
                    PreferencesHelper.setPreferenceBoolean(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_LOGGED_IN,true);
                    PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_NAME,name);
                    PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_USER_NAME,userName);
                    PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_CITY,city);
                    PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_DOB,dob);
                    if (!(gender.equals(""))){
                        PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_GENDER,gender);
                    }

                    PreferencesHelper.setPreference(CompleteSignupActivity.this,PreferencesHelper.PREFERENCE_DOB,dob);
                }


            }
        });

    }



}

