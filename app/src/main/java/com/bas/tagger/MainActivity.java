package com.bas.tagger;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends Activity {
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ArrayList<ScanFilter> mScanFilter = new ArrayList<ScanFilter>();
    private ScanSettings mScanSettings;
    private AdvertiseData mAdvertiseData;
    private AdvertiseSettings mAdvertiseSettings;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;


    BeaconListAdapter adapter;

    private final String TAG = "TAGGER";

    protected ScanCallback mScanCallback = new ScanCallback() {

        ArrayList<String>nodeids = new ArrayList<String>();
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            byte[] scanData = result.getScanRecord().getBytes();
            if (scanData[7] == 0x02 && scanData[8] == 0x15) { // iBeacon indicator
               // System.out.println("iBeacon Packet: %s", bytesToHexString(scanData));
                UUID uuid = getGuidFromByteArray(Arrays.copyOfRange(scanData, 9, 25));
                int major = (scanData[25] & 0xff) * 0x100 + (scanData[26] & 0xff);
                int minor = (scanData[27] & 0xff) * 0x100 + (scanData[28] & 0xff);
                byte txpw = scanData[29];
                System.out.println("iBeacon Major = " + major + " | Minor = " + minor + " TxPw " + (int)txpw + " | UUID = " + uuid.toString());


                Log.d(TAG,uuid.toString());
                if (!nodeids.contains(uuid.toString()))
                    adapter.add(new Node(R.mipmap.ic_launcher,uuid.toString(),major,minor));
                nodeids.add(uuid.toString());
            }

        }
    };



    public static String bytesToHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();
        for(int i=0; i<bytes.length; i++) {
            buffer.append(String.format("%02x", bytes[i]));
        }
        return buffer.toString();
    }
    public static UUID getGuidFromByteArray(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());
        return uuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this.getApplicationContext(), WebViewActivity.class);
//        startActivity(intent);

        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        //mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        setScanSettings();
        mBluetoothLeScanner.startScan(mScanFilter, mScanSettings, mScanCallback);
        Node[] nodes = {};
        adapter=new BeaconListAdapter(this,R.layout.beaconlist);
        ListView list=(ListView)findViewById(R.id.android_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(adapter);


//        setAdvertiseData();
//        setAdvertiseSettings();
//
//        if (mBluetoothLeAdvertiser != null) {
//            mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings, mAdvertiseData, new AdvertiseCallback() {
//                @Override
//                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                    super.onStartSuccess(settingsInEffect);
//                    Log.d(TAG, "Started Advertising");
//                }
//
//                @Override
//                public void onStartFailure(int errorCode) {
//                    super.onStartFailure(errorCode);
//                }
//            });
//        }


    }

    /**
     * bytesToHex method
     * Found on the internet
     * http://stackoverflow.com/a/9855338
     */
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public byte[] getIdAsByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private void setScanSettings() {
        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
        mBuilder.setReportDelay(0);
        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        mScanSettings = mBuilder.build();
    }

    protected void setAdvertiseData() {
        AdvertiseData.Builder mBuilder = new AdvertiseData.Builder();
        ByteBuffer mManufacturerData = ByteBuffer.allocate(24);
        byte[] uuid = getIdAsByte(UUID.fromString("4d87fc72-7adc-11e5-8bcf-feff819cdc9f"));
        mManufacturerData.put(0, (byte)0xBE); // Beacon Identifier
        mManufacturerData.put(1, (byte)0xAC); // Beacon Identifier
        for (int i=2; i<=17; i++) {
            mManufacturerData.put(i, uuid[i-2]); // adding the UUID
        }
        mManufacturerData.put(18, (byte)0x00); // first byte of Major
        mManufacturerData.put(19, (byte)0x09); // second byte of Major
        mManufacturerData.put(20, (byte)0x00); // first minor
        mManufacturerData.put(21, (byte)0x06); // second minor
        mManufacturerData.put(22, (byte)0xB5); // txPower
        mBuilder.addManufacturerData(224, mManufacturerData.array()); // using google's company ID
        mAdvertiseData = mBuilder.build();
    }

    protected void setAdvertiseSettings() {
        AdvertiseSettings.Builder mBuilder = new AdvertiseSettings.Builder();
        mBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        mBuilder.setConnectable(false);
        mBuilder.setTimeout(0);
        mBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        mAdvertiseSettings = mBuilder.build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
