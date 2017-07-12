package com.speedata.pasm;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.speedata.libutils.ConfigUtils;
import com.speedata.libutils.DataConversionUtils;
import com.speedata.libutils.ReadBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import speedatacom.a3310libs.PsamManager;
import speedatacom.a3310libs.inf.IPsam;

public class MainActivity extends Activity implements View.OnClickListener {

    //19200 9600
    private Button btn1Activite, btn2Activite, btnGetRomdan, btnSendAdpu, btnClear;
    private EditText edvADPU;
    private TextView tvShowData;
    private int psamflag = 0;
    private Context mContext;
    private TextView tvVerson;
    private TextView tvConfig;
    private ImageView imgReset;
    private Button btnOriginalCmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        initUI();
        showConfig();
        initDevice();
    }

    private void showConfig() {

        String verson = getVersion();
        tvVerson.setText("V" + verson);

        boolean isExit = ConfigUtils.isConfigFileExists();
        if (isExit)
            tvConfig.setText("�������ã�\n");
        else
            tvConfig.setText("��׼���ã�\n");
        ReadBean readBean = ConfigUtils.readConfig(this);
        ReadBean.PasmBean pasm = readBean.getPasm();
        String gpio = "";
        List<Integer> gpio1 = pasm.getGpio();
        for (Integer s : gpio1) {
            gpio += s + ",";
        }
        tvConfig.append("����:" + pasm.getSerialPort() + "  �����ʣ�" + pasm.getBraut() + " �ϵ�����:" +
                pasm.getPowerType() + " GPIO:" + gpio + " resetGpio:" + pasm.getResetGpio());
    }


    private void initUI() {
        setContentView(R.layout.activity_main);
        imgReset = (ImageView) findViewById(R.id.img_reset);
        imgReset.setOnClickListener(this);
        tvConfig = (TextView) findViewById(R.id.tv_config);
        btn1Activite = (Button) findViewById(R.id.btn1_active);
        btn2Activite = (Button) findViewById(R.id.btn2_active);
        btnGetRomdan = (Button) findViewById(R.id.btn_get_ramdon);
        btnSendAdpu = (Button) findViewById(R.id.btn_send_adpu);
        btnClear = (Button) findViewById(R.id.btn_clear);
        tvVerson = (TextView) findViewById(R.id.tv_verson);
        btnOriginalCmd = (Button) findViewById(R.id.btn_original_cmd);
        btnOriginalCmd.setOnClickListener(this);
        btn1Activite.setOnClickListener(this);
        btn2Activite.setOnClickListener(this);
        btnGetRomdan.setOnClickListener(this);
        btnSendAdpu.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        tvShowData = (TextView) findViewById(R.id.tv_show_message);
        tvShowData.setMovementMethod(ScrollingMovementMethod.getInstance());
        edvADPU = (EditText) findViewById(R.id.edv_adpu_cmd);
        edvADPU.setText("0084000008");
        edvADPU.setText("0020000003");
        edvADPU.setText("80f0800008122a31632a3bafe0");
        edvADPU.setText("00A404000BA000000003454E45524759");
//        edvADPU.setText("aabb0600000002010704");
    }


    String send_data = "";
    //��ȡpsamʵ��
    IPsam psamIntance = PsamManager.getPsamIntance();


    @Override
    public void onClick(View v) {
        if (v == imgReset) {
            psamIntance.resetDev();
        } else if (v == btn1Activite) {
            psamflag = 1;
            byte[] data = psamIntance.PsamPower(IPsam.PowerType.Psam1);
            if (data != null)
                tvShowData.setText("Psam1 activite \n" + DataConversionUtils.byteArrayToString
                        (data));
            else {
                tvShowData.setText("failed");
            }
        } else if (v == btn2Activite) {
            psamflag = 2;
            byte[] data = psamIntance.PsamPower(IPsam.PowerType.Psam2);
            if (data != null)
                tvShowData.setText("Psam2 activite \n" + DataConversionUtils.byteArrayToString
                        (data));
            else {
                tvShowData.setText("failed");
            }
        } else if (v == btnGetRomdan) {
            if (psamflag == 1) {
                try {
                    tvShowData.setText("Psam1 Send data��00 84 00 00 04\n");
                    byte[] data = psamIntance.WriteCmd(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                            0x04}, IPsam
                            .PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                    else
                        return;
                    //���Ϳ� Ӧ�ý���
                  /*  byte[] icv = new byte[8];
                    System.arraycopy(data, 0,icv,0,4);
                    System.arraycopy(new byte[]{0,0,0,0}, 0,icv,4,4);
                    byte[] mac = Pboc3DesMACUtils.getMac(icv);
                    tvShowData.append("\nicv->" + DataConversionUtils.byteArrayToString(icv));
                    tvShowData.append("\nmac->" + DataConversionUtils.byteArrayToString(mac));
                    //84180004+mac
                    byte[] temp = new byte[9];
                    temp[0] = (byte) 0x84;
                    temp[1] = 0x18;
                    temp[2] = 0x00;
                    temp[3] = 0x00;
                    temp[4] = 0x04;
                    System.arraycopy(mac, 0, temp, 5, 4);
                    tvShowData.append("\nsend->" + DataConversionUtils.byteArrayToString(temp));
                    data = psamIntance.WriteCmd(temp, IPsam
                            .PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("\nrece->" + DataConversionUtils.byteArrayToString(data));
                    */
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else if (psamflag == 2) {
                try {
                    tvShowData.setText("Psam2 Send data��00 84 00 00 08\n");
                    byte[] data = psamIntance.WriteCmd(new byte[]{0x00, (byte) 0x84, 0x00, 0x00,
                            0x08}, IPsam
                            .PowerType.Psam2);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnSendAdpu) {
            String temp_cmd = edvADPU.getText().toString();
            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
                Toast.makeText(mContext, "Please enter a valid instruction��", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            send_data = temp_cmd;
            if (psamflag == 1) {
                tvShowData.setText("Psam1 Send data��\n" + send_data + "\n");
                try {
                    byte[] data = psamIntance.WriteCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else if (psamflag == 2) {
                tvShowData.setText("Psam2 Send data��\n" + send_data + "\n");
                try {
                    byte[] data = psamIntance.WriteCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam2);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }else if (v == btnOriginalCmd) {
            String temp_cmd = edvADPU.getText().toString();
            if ("".equals(temp_cmd) || temp_cmd.length() % 2 > 0 || temp_cmd.length() < 4) {
                Toast.makeText(mContext, "Please enter a valid instruction��", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            send_data = temp_cmd;
            if (psamflag == 1) {
                tvShowData.setText("Psam1 Send data��\n" + send_data + "\n");
                try {
                    byte[] data = psamIntance.WriteOriginalCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam1);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            } else if (psamflag == 2) {
                tvShowData.setText("Psam2 Send data��\n" + send_data + "\n");
                try {
                    byte[] data = psamIntance.WriteOriginalCmd(DataConversionUtils
                            .HexString2Bytes(temp_cmd), IPsam.PowerType.Psam2);
                    if (data != null)
                        tvShowData.append("rece->" + DataConversionUtils.byteArrayToString(data));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnClear) {
            tvShowData.setText("");
        }

    }


    private void initDevice() {
        try {
            psamIntance.initDev(this);//��ʼ���豸
            psamIntance.resetDev();//��λ
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            psamIntance.releaseDev();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ��ǰӦ�ó���İ汾��
     */
    private String getVersion() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "�汾�Ŵ���";
        }
    }
}
