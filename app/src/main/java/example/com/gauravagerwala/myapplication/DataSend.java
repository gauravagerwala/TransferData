package example.com.gauravagerwala.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSend extends Activity {

        private final String TAG = DataSend.class.getSimpleName();

        private static UsbSerialPort sPort = null;


        private TextView mTitleTextView;
        private TextView mDumpTextView;
        private ScrollView mScrollView;

        private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();


        private SerialInputOutputManager mSerialIoManager;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.data_send);
            mTitleTextView = (TextView) findViewById(R.id.demoTitle);
            mDumpTextView = (TextView) findViewById(R.id.consoleText);
            mScrollView = (ScrollView) findViewById(R.id .demoScroller);
            startIoManager();
            Log.d(TAG, "Resumed, port=" + sPort);
            if (sPort == null) {
                mTitleTextView.setText("No serial device.");
            } else {
                final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

                UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
                if (connection == null) {
                    mTitleTextView.append("Opening device failed");
                    return;
                }

                try {
                    sPort.open(connection);
                    sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                    mTitleTextView.setText("Error opening device: " + e.getMessage());
                    try {
                        sPort.close();
                    } catch (IOException e2) {
                        // Ignore.
                    }
                    sPort = null;
                    return;
                }
                mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
            }
            onDeviceStateChange();
        }

      /*  @Override
        protected void onPause() {
            super.onPause();
            stopIoManager();
            if (sPort != null) {
                try {
                    sPort.close();
                } catch (IOException e) {
                    // Ignore.
                }
                sPort = null;
            }
            finish();
        }

        @Override
        protected void onResume() {
            super.onResume();
            Log.d(TAG, "Resumed, port=" + sPort);
            if (sPort == null) {
                mTitleTextView.setText("No serial device.");
            } else {
                final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

                UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
                if (connection == null) {
                    mTitleTextView.setText("Opening device failed");
                    return;
                }

                try {
                    sPort.open(connection);
                    sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                    mTitleTextView.setText("Error opening device: " + e.getMessage());
                    try {
                        sPort.close();
                    } catch (IOException e2) {
                        // Ignore.
                    }
                    sPort = null;
                    return;
                }
                mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
            }
            onDeviceStateChange();
        }*/

        private void stopIoManager() {
            if (mSerialIoManager != null) {
                Log.i(TAG, "Stopping io manager ..");
                mSerialIoManager.stop();
                mSerialIoManager = null;
            }
        }

        private void startIoManager() {
            byte[] data = {'A', 'B'};
            if (sPort == null) {
                Log.i(TAG, "Starting io manager ..");
                mSerialIoManager = new SerialInputOutputManager(sPort);
                mSerialIoManager.writeAsync(data);
                mExecutor.submit(mSerialIoManager);
            }
        }

        private void onDeviceStateChange() {
            stopIoManager();
            startIoManager();
        }

     /* private void updateReceivedData(byte[] data) {
            final String message = "Read " + data.length + " bytes: \n"
                    + HexDump.dumpHexString(data) + "\n\n";
            mDumpTextView.append(message);
            mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
        }*/


        public static void show(Context context, UsbSerialPort port) {
            sPort = port;
            final Intent intent = new Intent(context, DataSend.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
    }
