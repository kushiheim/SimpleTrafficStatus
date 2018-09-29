package kushiheim.example.com.simpletrafficstatus;

import android.annotation.SuppressLint;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;

    private TextView RX;
    private TextView TX;
    private TextView ssid;
    private TextView bssid;
    private TextView ip_address;
    private TextView mac_address;
    private TextView network_id;
    private TextView link_speed;
    private WifiManager mWifiManager;
    private TextView mtu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        // for Wifi Information
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        ssid.setText(wifiInfo.getSSID());
        bssid.setText(wifiInfo.getBSSID());
        int ipAddress = wifiInfo.getIpAddress();
        String ip_addr = ((ipAddress >> 0) & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) +
                "." + ((ipAddress >> 16) & 0xFF) + "." + ((ipAddress >> 24) & 0xFF);
        ip_address.setText(ip_addr);
        mac_address.setText(wifiInfo.getMacAddress());
        network_id.setText(String.valueOf(wifiInfo.getNetworkId()));
        link_speed.setText(String.valueOf(wifiInfo.getLinkSpeed()));

        // for MTU
        try {
            Enumeration<NetworkInterface> nets =
                    NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                displayInterfaceInformation(netint);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // for RX, TX
        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh oh");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

    }

    /**
     * https://www.java-tips.org/java-se-tips-100019/31-java-net/1767-programmatic-access-to-network-parameters.html
     */
    @SuppressLint("SetTextI18n")
    private void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        mtu.setText(mtu.getText() + "\n" +
                            String.format("Display name : %s%n", netint.getDisplayName()) +
                            String.format("MTU : %s%n", netint.getMTU()));
    }

    private void setupViews() {
        RX = (TextView) findViewById(R.id.RX);
        TX = (TextView) findViewById(R.id.TX);
        ssid = (TextView) findViewById(R.id.ssid);
        bssid = (TextView) findViewById(R.id.bssid);
        ip_address = (TextView) findViewById(R.id.ip_address);
        mac_address = (TextView) findViewById(R.id.mac_address);
        network_id = (TextView) findViewById(R.id.network_id);
        link_speed = (TextView) findViewById(R.id.link_speed);
        mtu = (TextView) findViewById(R.id.mtu);
    }

    private final Runnable mRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            RX.setText(Long.toString(rxBytes));
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            TX.setText(Long.toString(txBytes));
            mHandler.postDelayed(mRunnable, 1000);
        }
    };


}
