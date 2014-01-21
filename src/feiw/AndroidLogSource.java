package feiw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AndroidLogSource extends LogSource {

    Process mAdbProcess;
    public AndroidLogSource() throws DeviceNotConnected {
        super();
        setStatus(stConnecting);

            try {
                mAdbProcess = Runtime.getRuntime().exec(SystemConfigs.getAdbPath() + "  logcat -vthreadtime");
                final BufferedReader rd = new BufferedReader(new InputStreamReader(mAdbProcess.getInputStream()));
                String s = rd.readLine();
                if (s.contains("waiting for device")) {
                    throw new DeviceNotConnected("Device not connected");
                }
                addLogItem(s, false);
                setStatus(stConnected);
                new Thread() {
                    public void run() {
                        try {
                             fetchLogs(mAdbProcess.getInputStream());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            setStatus(stIdle);
                        }
                    }
                }.start();
          
            } catch (IOException e1) {
                throw new DeviceNotConnected("Device not connected");
            }
 

    }
    
    public void disconnect() {
        if (mAdbProcess != null) {
            mAdbProcess.destroy();
            mAdbProcess = null;
            setStatus(stIdle);
        }
 
    }
}
