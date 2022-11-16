package nguyenleduan.app.mymotorcycle;

import android.bluetooth.BluetoothSocket;
import android.os.Vibrator;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class DataSetting {

    public static boolean isConnect = false;
    public static  String KeytimeSearch = "KeytimeSearch";
    public static  String KeyAddress = "KeyAddress";
    public static  String KeyStartUp= "KeyStartUp";
    public static  String KeyCoi = "KeyCoi";
    public static  String KeyPower = "KeyPower";
    public static  String KeyScreen = "KeyScreen";
    public static  String KeyFunction = "KeyFunction";
    public static String addressConnect = "";
    public static int timeSearch = 5;
    public static BluetoothSocket btSocket = null;
    public static Vibrator mVibrator;
    public static List<String> listCoi = Arrays.asList("Chọn setting","Nhịp 1","Nhịp 2","Nhịp 3","Giữ 1s","Giữ 2s","Giữ 3s","Giữ 4s","Giữ 5s" );
    public static List<String> listStartUp = Arrays.asList("Chọn setting","Đề 1s","Đề 2s","Đề 3s","Đề 4s","Đề 5s","Đề 6s","Đề 7s","Đề 8s");
    public static List<String> listScreen = Arrays.asList("Không sử dụng","Chỉ quét xe","Quét và bật nguồn","Quét và đề 2s","Quét và Còi 1s");
    public static List<String> listFunction = Arrays.asList("Không sử dụng","Đề xe 1s","Đề xe 2s","Đề xe 3s","Còi nhịp 1","Còi giữa 1s","Còi giữa 2s");
    public static boolean isLock = false;
    public static boolean isDisconnect = false;
    public static String mLock = "Lock";
    public static String mCoi = "Nhịp 1";
    public static String mStartUp = "Đề 1s";
    public static String mPower = "Power 1";
    public static String mFunction = "Đề xe 1s";
    public static String mScreen = "";

    public String returnData(String data){
        Log.d("------------------- Send data Name ",data);
        switch ( data ) {
            case  "Nhịp 1":
                return "2"; // 50
            case  "Nhịp 2":
                return "3"; // 51
            case  "Nhịp 3":
                return "4"; // 52
            case  "Giữ 1s":
                return "5"; // 53
            case  "Giữ 2s":
                return "6"; // 54
            case  "Giữ 3s":
                return "7"; // 55
            case  "Giữ 4s":
                return "8"; // 56
            case  "Giữ 5s":
                return "9"; // 57
                ////////////////////////////////
            case  "Đề 1s":
                return "a"; // 97
            case  "Đề 2s":
                return "b"; // 98
            case  "Đề 3s":
                return "c"; // 99
            case  "Đề 4s":
                return "d"; // 100
            case  "Đề 5s":
                return "e"; // 101
            case  "Đề 6s":
                return "f"; // 102
            case  "Đề 7s":
                return "g"; // 103
            case  "Đề 8s":
                return "h"; // 104
            //////////////////////////////////
            case  "Power 1":
                return "i"; // 105
            case  "Power 2":
                return "k"; // 107
            case  "Power 3":
                return "l";
                ////////////// window
            case  "Đề xe 2s":
                return "o";
            case  "Đề xe 1s":
                return "z";
            case  "Đề xe 3s":
                return "W";
            case  "Còi nhịp 1":
                return "q";
            case  "Còi giữa 1s":
                return "y";
            case  "Còi giữa 2s":
                return "y";
                //////////////
            case  "Chỉ quét xe":
                return "m";
            case  "Quét và bật nguồn":
                return "n";
            case  "Quét và đề 2s":
                return "o";
            case  "Quét và Còi 1s":
                return "p";
            case  "Không sử dụng":
                return "r";
            //////////////////////////
            case  "Lock":
                return "v";
            case  "Unlock":
                return "t";
        }
        return "x"; // 120
    }

    public int getIndexList(List<String> list, String content){
        for(int i =0;i<list.size();i++){
            if(list.get(i).equals(content+""))
                return i;
        }
        return 0;
    }
}
