package mpip.finki.ukim.googlenearbyplaces.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MyResultReceiver<T> extends ResultReceiver {
    public static final int RESULT_CODE_OK = 1100;

    private ResultReceiverCallBack mReceiver;

    public MyResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(ResultReceiverCallBack<T> receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null && resultCode == RESULT_CODE_OK) {
            mReceiver.onSuccess(resultData.getSerializable("a"));
        }
    }

    public interface ResultReceiverCallBack<T>{
        public void onSuccess(T data);
    }
}
