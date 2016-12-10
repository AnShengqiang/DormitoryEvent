package com.charger.android.dormtoryevents;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by a1877 on 2016/11/7.
 */

public class EventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Event mEvent;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSovedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    /*required interface for hosting activities*/
    public interface Callbacks{
        void onEventUpdated(Event event);
    }

    public static EventFragment newInstance(UUID eventId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, eventId);

        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID eventId = (UUID) getArguments().getSerializable(ARG_EVENT_ID);

        mEvent = EventLab.get(getActivity()).getEvent(eventId);

        mPhotoFile = EventLab.get(getActivity()).getPhotoFile(mEvent);
    }

    @Override
    public void onPause(){
        super.onPause();

        EventLab.get(getActivity())
                .updateEvent(mEvent);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mTitleField = (EditText) v.findViewById(R.id.event_title);
        mTitleField.setText(mEvent.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //这里故意留白
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setTitle(s.toString());
                updateEvent();
            }

            @Override
            public void afterTextChanged(Editable s) {
                //这里故意留白
            }
        });

        mDateButton = (Button)v.findViewById(R.id.event_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mEvent.getDate());
                dialog.setTargetFragment(EventFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSovedCheckBox = (CheckBox)v.findViewById(R.id.event_solved);
        mSovedCheckBox.setChecked(mEvent.isSolved());
        mSovedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEvent.setSolved(isChecked);
                updateEvent();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.event_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getEventReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.event_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.event_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mEvent.getSuspect() != null){
            mSuspectButton.setText(mEvent.getSuspect());

        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.event_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.event_photo);
        updatePhotoView();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode!= Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DATE){
            Date date = (Date)data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mEvent.setDate(date);
            updateEvent();
            updateDate();
        }else if(requestCode == REQUEST_CONTACT){
            Uri contactUri = data.getData();
            //
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try{
                if (c.getCount() == 0){
                    return;
                }

                //
                c.moveToFirst();
                String suspect = c.getString(0);
                mEvent.setSuspect(suspect);
                updateEvent();
                mSuspectButton.setText(suspect);
            }finally {
                c.close();
            }
        }else if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
            updateEvent();
        }
    }

    private void updateEvent(){
        EventLab.get(getActivity()).updateEvent(mEvent);
        mCallbacks.onEventUpdated(mEvent);
    }

    private void updateDate() {
        mDateButton.setText(mEvent.getDate().toString());
    }

    private String getEventReport(){
        String solvedString = null;
        if (mEvent.isSolved()){
            solvedString = getString(R.string.event_report_solved);
        }else {
            solvedString = getString(R.string.event_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mEvent.getDate()).toString();

        String suspect = mEvent.getSuspect();
        if (suspect == null){
            suspect = getString(R.string.event_report_no_suspect);
        }else {
            suspect = getString(R.string.event_report_suspect, suspect);
        }

        String report = getString(R.string.event_report,
                mEvent.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView(){
        if (mPhotoView == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
