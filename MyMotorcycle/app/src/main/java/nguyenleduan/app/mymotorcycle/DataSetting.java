package nguyenleduan.app.mymotorcycle;

import android.bluetooth.BluetoothSocket;

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
    public static String addressConnect = "";
    public static int timeSearch = 20;
    public static BluetoothSocket btSocket = null;
    public static List<String> listCoi = Arrays.asList("Chọn setting","Nhịp 1","Nhịp 2","Nhịp 3","Giữ 1s","Giữ 2s","Giữ 3s","Giữ 4s","Giữ 5s" );
    public static List<String> listStartUp = Arrays.asList("Chọn setting","Đề 1s","Đề 2s","Đề 3s","Đề 4s","Đề 5s","Đề 6s","Đề 7s","Đề 8s");
    public static List<String> listScreen = Arrays.asList("Chọn setting","Chỉ quét xe","Quét và bật nguồn","Quét và đề 3s","Quét và Còi 1s");
    public static String mCoi = "";
    public static String mStartUp = "";
    public static String mPower = "";
    public static String mScreen = "";

    public String returnData(String data){

        switch ( data ) {
            case  "Nhịp 1":
                return "2";
            case  "Nhịp 2":
                return "3";
            case  "Nhịp 3":
                return "4";
            case  "Giữ 1s":
                return "5";
            case  "Giữ 2s":
                return "6";
            case  "Giữ 3s":
                return "7";
            case  "Giữ 4s":
                return "8";
            case  "Giữ 5s":
                return "9";
                ////////////////////////////////
            case  "Start 1s":
                return "a";
            case  "Start 2s":
                return "b";
            case  "Start 3s":
                return "c";
            case  "Start 4s":
                return "d";
            case  "Start 5s":
                return "e";
            case  "Start 6s":
                return "f";
            case  "Start 7s":
                return "g";
            case  "Start 8s":
                return "h";
            //////////////////////////////////
            case  "Power 1":
                return "i";
            case  "Power 2":
                return "k";
            case  "Power 3":
                return "l";
                //////////////
            case  "Chỉ quét xe":
                return "m";
            case  "Quét và bật nguồn":
                return "n";
            case  "Quét và đề 3s":
                return "o";
            case  "Quét và Còi 1s":
                return "p";
//            case  "Power 3":
//                return "q";
        }
        return "x";
    }

    public int getIndexList(List<String> list, String content){
        for(int i =0;i<list.size();i++){
            if(list.get(i).equals(content+""))
                return i;
        }
        return 0;
    }
}
