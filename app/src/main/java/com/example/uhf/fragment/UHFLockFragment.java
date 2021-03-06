package com.example.uhf.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.uhf.R;
import com.example.uhf.activity.UHFMainActivity;
import com.example.uhf.tools.UIHelper;
import com.rscja.deviceapi.interfaces.IUHF;


public class UHFLockFragment extends KeyDwonFragment implements  OnClickListener {

    private static final String TAG = "UHFLockFragment";
    private UHFMainActivity mContext;

    private EditText EtAccessPwd_Lock;
    private EditText etLockCode;
    private EditText etPtr_filter_lock;
    private EditText etLen_filter_lock;
    private EditText etData_filter_lock;

    private Button btnLock;
    private CheckBox cb_filter_lock;

    private RadioButton rbEPC_filter_lock;
    private RadioButton rbTID_filter_lock;
    private RadioButton rbUser_filter_lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.uhf_lock_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (UHFMainActivity) getActivity();

        etLockCode = (EditText) getView().findViewById(R.id.etLockCode);
        EtAccessPwd_Lock = (EditText) getView().findViewById(R.id.EtAccessPwd_Lock);
        etPtr_filter_lock = (EditText) getView().findViewById(R.id.etPtr_filter_lock);
        etLen_filter_lock = (EditText) getView().findViewById(R.id.etLen_filter_lock);
        etData_filter_lock = (EditText) getView().findViewById(R.id.etData_filter_lock);

        btnLock = (Button) getView().findViewById(R.id.btnLock);
        cb_filter_lock = (CheckBox) getView().findViewById(R.id.cb_filter_lock);

        rbEPC_filter_lock = (RadioButton) getView().findViewById(R.id.rbEPC_filter_lock);
        rbTID_filter_lock = (RadioButton) getView().findViewById(R.id.rbTID_filter_lock);
        rbUser_filter_lock = (RadioButton) getView().findViewById(R.id.rbUser_filter_lock);

        rbEPC_filter_lock.setOnClickListener(this);
        rbTID_filter_lock.setOnClickListener(this);
        rbUser_filter_lock.setOnClickListener(this);

        cb_filter_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String data = etData_filter_lock.getText().toString().trim();
                    String rex = "[\\da-fA-F]*"; //???????????????????????????????????????????????????
                    if (data == null || data.isEmpty() || !data.matches(rex)) {
                        UIHelper.ToastMessage(mContext, "??????????????????????????????????????????");
                        cb_filter_lock.setChecked(false);
                        return;
                    }
                }
            }
        });

        etLockCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.tvLockCode);
                final View vv = LayoutInflater.from(mContext).inflate(R.layout.uhf_dialog_lock_code, null);
                builder.setView(vv);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        etLockCode.getText().clear();
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioButton rbOpen = (RadioButton) vv.findViewById(R.id.rbOpen);
                        RadioButton rbLock = (RadioButton) vv.findViewById(R.id.rbLock);
                        CheckBox cbPerm = (CheckBox) vv.findViewById(R.id.cbPerm);

                        CheckBox cbKill = (CheckBox) vv.findViewById(R.id.cbKill);
                        CheckBox cbAccess = (CheckBox) vv.findViewById(R.id.cbAccess);
                        CheckBox cbEPC = (CheckBox) vv.findViewById(R.id.cbEPC);
                        CheckBox cbTid = (CheckBox) vv.findViewById(R.id.cbTid);
                        CheckBox cbUser = (CheckBox) vv.findViewById(R.id.cbUser);
                        String mask = "";
                        String value = "";
                        int[] data = new int[20];
                        if (cbUser.isChecked()) {
                            data[11] = 1;
                            if (cbPerm.isChecked()) {
                                data[0] = 1;
                                data[10] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[1] = 1;
                            }
                        }
                        if (cbTid.isChecked()) {
                            data[13] = 1;
                            if (cbPerm.isChecked()) {
                                data[12] = 1;
                                data[2] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[3] = 1;
                            }
                        }
                        if (cbEPC.isChecked()) {
                            data[15] = 1;
                            if (cbPerm.isChecked()) {
                                data[14] = 1;
                                data[4] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[5] = 1;
                            }
                        }
                        if (cbAccess.isChecked()) {
                            data[17] = 1;
                            if (cbPerm.isChecked()) {
                                data[16] = 1;
                                data[6] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[7] = 1;
                            }
                        }
                        if (cbKill.isChecked()) {
                            data[19] = 1;
                            if (cbPerm.isChecked()) {
                                data[18] = 1;
                                data[8] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[9] = 1;
                            }
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("0000");
                        for (int k = data.length - 1; k >= 0; k--) {
                            stringBuffer.append(data[k] + "");
                        }

                        String code = binaryString2hexString(stringBuffer.toString());
                        Log.i(UHFMainActivity.TAG, "  tempCode=" + stringBuffer.toString() + "  code=" + code);

                        etLockCode.setText(code.replace(" ", "0") + "");
                    }
                });
                builder.create().show();
            }
        });

//        etLockCode.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder= new AlertDialog.Builder(mContext);
//
//                builder.setTitle(R.string.tvLockCode);
//                builder.create().show();
//            }
//        });

        btnLock.setOnClickListener(new btnLockOnClickListener());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbEPC_filter_lock:
                etPtr_filter_lock.setText("32");
                break;
            case R.id.rbTID_filter_lock:
                etPtr_filter_lock.setText("0");
                break;
            case R.id.rbUser_filter_lock:
                etPtr_filter_lock.setText("0");
                break;
        }
    }

    public class btnLockOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            String strPWD = EtAccessPwd_Lock.getText().toString().trim();// ????????????
            String strLockCode = etLockCode.getText().toString().trim();

            if (!TextUtils.isEmpty(strPWD)) {
                if (strPWD.length() != 8) {
                    UIHelper.ToastMessage(mContext, R.string.uhf_msg_addr_must_len8);
                    return;
                } else if (!mContext.vailHexInput(strPWD)) {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nohex);
                    return;
                }
            } else {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nopwd);
                return;
            }

            if (TextUtils.isEmpty(strLockCode)) {
                UIHelper.ToastMessage(mContext, R.string.rfid_mgs_error_nolockcode);
                return;
            }
            boolean result = false;
            if (cb_filter_lock.isChecked()) {
                String filterData = etData_filter_lock.getText().toString();
                if (filterData == null || filterData.isEmpty()) {
                    UIHelper.ToastMessage(mContext, "????????????????????????!");
                    return;
                }
                if (etPtr_filter_lock.getText().toString() == null || etPtr_filter_lock.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "??????????????????????????????");
                    return;
                }
                if (etLen_filter_lock.getText().toString() == null || etLen_filter_lock.getText().toString().isEmpty()) {
                    UIHelper.ToastMessage(mContext, "??????????????????????????????");
                    return;
                }
                int filterPtr = Integer.parseInt(etPtr_filter_lock.getText().toString());
                int filterCnt = Integer.parseInt(etLen_filter_lock.getText().toString());
                int filterBank = 0;
                if (rbEPC_filter_lock.isChecked()) {
                    filterBank = IUHF.Bank_EPC;
                } else if (rbTID_filter_lock.isChecked()) {
                    filterBank = IUHF.Bank_TID;
                } else if (rbUser_filter_lock.isChecked()) {
                    filterBank = IUHF.Bank_USER;
                }

                if (mContext.mReader.lockMem(strPWD,
                        filterBank,
                        filterPtr,
                        filterCnt,
                        filterData,
                        strLockCode)) {
                    result = true;
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_succ);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_fail);
                }
            } else {
                if (mContext.mReader.lockMem(strPWD, strLockCode)) {
                    result = true;
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_succ);
                } else {
                    UIHelper.ToastMessage(mContext, R.string.rfid_mgs_lock_fail);
                }
            }
            if (!result) {
                mContext.playSound(2);
            } else {
                mContext.playSound(1);
            }

        }
    }

    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

}
