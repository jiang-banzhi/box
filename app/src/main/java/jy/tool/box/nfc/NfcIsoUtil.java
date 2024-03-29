package jy.tool.box.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 * @author : jiang
 * @time : 2021/7/28.
 * @desciption :
 * @version :
 * </pre>
 */

class NfcIsoUtil {

    protected final static byte TRANS_CSU = 6; // 如果等于0x06或者0x09，表示刷卡；否则是充值
    protected final static byte TRANS_CSU_CPX = 9; // 如果等于0x06或者0x09，表示刷卡；否则是充值

    public static void praseIntent(Intent intent, TextView show_msg) {

        String nfcAction = intent.getAction(); // 解析该Intent的Action
//        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(nfcAction)) {
            Log.d("h_bl", "ACTION_TECH_DISCOVERED");
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); // 获取Tag标签，既可以处理相关信息
            for (String tech : tag.getTechList()) {
                Log.d("h_bl", "tech=" + tech);
            }
            IsoDep isoDep = IsoDep.get(tag);
            String str = "";
            try {
                isoDep.connect(); // 连接
                if (isoDep.isConnected()) {
                    Log.d("h_bl", "isoDep.isConnected"); // 判断是否连接上
                    // 1.select PSF (1PAY.SYS.DDF01)
                    // 选择支付系统文件，它的名字是1PAY.SYS.DDF01。
                    byte[] DFN_PSE = { (byte) '1', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y', (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1', };
                    isoDep.transceive(getSelectCommand(DFN_PSE));
                    // 2.选择公交卡应用的名称
                    byte[] DFN_SRV = { (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x86, (byte) 0x98, (byte) 0x07, (byte) 0x01, };
                    isoDep.transceive(getSelectCommand(DFN_SRV));
                    // 3.读取余额
                    byte[] ReadMoney = { (byte) 0x80, // CLA Class
                            (byte) 0x5C, // INS Instruction
                            (byte) 0x00, // P1 Parameter 1
                            (byte) 0x02, // P2 Parameter 2
                            (byte) 0x04, // Le
                    };
                    byte[] Money = isoDep.transceive(ReadMoney);
                    if (Money != null && Money.length > 4) {
                        int cash = byteToInt(Money, 4);
                        float ba = cash / 100.0f;
                        show_msg.setText("余额:" + ba);
                    }
                    // 4.读取所有交易记录
                    byte[] ReadRecord = { (byte) 0x00, // CLA Class
                            (byte) 0xB2, // INS Instruction
                            (byte) 0x01, // P1 Parameter 1
                            (byte) 0xC5, // P2 Parameter 2
                            (byte) 0x00, // Le
                    };
                    byte[] Records = isoDep.transceive(ReadRecord);
                    // 处理Record
                    Log.d("h_bl", "总消费记录" + Records);
                    ArrayList<byte[]> ret = parseRecords(Records);
                    List<String> retList = parseRecordsToStrings(ret);
                    show_msg.append("\n" + "消费记录如下：");
                    for (String string : retList) {
                        Log.d("h_bl", "消费记录" + string);
                        show_msg.append("\n" + string);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (isoDep != null) {
                    try {
                        isoDep.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    }
    public static byte byteToHex(byte arg) {
        byte hex = 0;
        if (arg >= 48 && arg <= 57) {
            hex = (byte) (arg - 48);
        } else if (arg >= 65 && arg <= 70) {
            hex = (byte) (arg - 55);
        } else if (arg >= 97 && arg <= 102) {
            hex = (byte) (arg - 87);
        }
        return hex;
    }

    private static byte[] getSelectCommand(byte[] aid) {
        final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
        cmd_pse.put((byte) 0x00) // CLA Class
                .put((byte) 0xA4) // INS Instruction
                .put((byte) 0x04) // P1 Parameter 1
                .put((byte) 0x00) // P2 Parameter 2
                .put((byte) aid.length) // Lc
                .put(aid).put((byte) 0x00); // Le
        return cmd_pse.array();
    }

    /**
     * 整条Records解析成ArrayList<byte[]>
     *
     * @param Records
     * @return
     */
    private static ArrayList<byte[]> parseRecords(byte[] Records) {
        int max = Records.length / 23;
        Log.d("h_bl", "消费记录有" + max + "条");
        ArrayList<byte[]> ret = new ArrayList<byte[]>();
        for (int i = 0; i < max; i++) {
            byte[] aRecord = new byte[23];
            for (int j = 23 * i, k = 0; j < 23 * (i + 1); j++, k++) {
                aRecord[k] = Records[j];
            }
            ret.add(aRecord);
        }
        for (byte[] bs : ret) {
            Log.d("h_bl", "消费记录有byte[]" + bs); // 有数据。解析正确。
        }
        return ret;
    }

    /**
     * ArrayList<byte[]>记录分析List<String> 一条记录是23个字节byte[] data，对其解码如下
     * data[0]-data[1]:index data[2]-data[4]:over,金额溢出?？？ data[5]-data[8]:交易金额
     * ？？代码应该是（5，4） data[9]:如果等于0x06或者0x09，表示刷卡；否则是充值
     * data[10]-data[15]:刷卡机或充值机编号
     * data[16]-data[22]:日期String.format("%02X%02X.%02X.%02X %02X:%02X:%02X"
     * ,data[16], data[17], data[18], data[19], data[20], data[21], data[22]);
     *
     * @param
     * @return
     */
    private static  List<String> parseRecordsToStrings(ArrayList<byte[]>... Records) {
        List<String> recordsList = new ArrayList<String>();
        for (ArrayList<byte[]> record : Records) {
            if (record == null)
                continue;
            for (byte[] v : record) {
                StringBuilder r = new StringBuilder();
                int cash = byteToInt(v,  4);
                char t = (v[9] == TRANS_CSU || v[9] == TRANS_CSU_CPX) ? '-' : '+';
                r.append(String.format("%02X%02X.%02X.%02X %02X:%02X ", v[16], v[17], v[18], v[19], v[20], v[21], v[22]));
                r.append("   " + t)
                        .append(cash / 100.0f)
                ;
                String aLog = r.toString();
                recordsList.add(aLog);
            }
        }
        return recordsList;
    }

    // byteArray转化为int
    private static int byteToInt(byte[] b, int n) {
        int ret = 0;
        for (int i = 0; i < n; i++) {
            ret = ret << 8;
            ret |= b[i] & 0x00FF;
        }
        if (ret > 100000 || ret < -100000)
            ret -= 0x80000000;
        return ret;
    }

}
