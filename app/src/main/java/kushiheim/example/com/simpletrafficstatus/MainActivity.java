package kushiheim.example.com.simpletrafficstatus;

import android.net.TrafficStats;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;

    private TextView RX;
    private TextView TX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RX = (TextView) findViewById(R.id.RX);
        TX = (TextView) findViewById(R.id.TX);

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

    private final Runnable mRunnable = new Runnable() {
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
