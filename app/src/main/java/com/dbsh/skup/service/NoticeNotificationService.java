package com.dbsh.skup.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.dbsh.skup.R;
import com.dbsh.skup.api.MajorNoticeApi;
import com.dbsh.skup.api.NoticeApi;
import com.dbsh.skup.model.NoticeData;
import com.dbsh.skup.client.NoticeClient;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeNotificationService extends LifecycleService {
    private NotificationManager notificationManager;
    private Notification notification;
	private Notification serviceNotification;

    private ServiceThread thread;

    private NoticeApi noticeApi;
    private MajorNoticeApi majorNoticeApi;

    private ArrayList<NoticeData> majorNoticeDataArrayList;

    public MutableLiveData<ArrayList<NoticeData>> noticeDataLiveData = new MutableLiveData<>();

    public static final String SERVICE_ID = "ForegroundServiceChannel";
    public static final String CHANNEL_ID = "NoticeNotificationChannel";
	public static final String GROUP_NAME = "noticeGroup";
	public static final int SUMMARY_ID = 1215;

    SharedPreferences notice;
    private int notificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
	    myServiceHandler handler = new myServiceHandler(Looper.getMainLooper());
	    thread = new ServiceThread(handler);

        noticeDataLiveData.observe(this, new Observer<ArrayList<NoticeData>>() {
            @Override
            public void onChanged(ArrayList<NoticeData> noticeData) {
                if(noticeData != null) {
                    notice = getSharedPreferences("notice", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor currentNotice = notice.edit();
                    int savedNoticeNumber = Integer.parseInt(notice.getString("noticeNumber", "0"));
                    int i = 0;
                    int j = 0;

                    System.out.println("저장된 최신 공지 : " + savedNoticeNumber);
                    for (NoticeData notice : noticeData) {
                        String url = notice.getUrl();
                        int startIndex = url.indexOf("srl");

                        if (savedNoticeNumber < Integer.parseInt(url.substring(startIndex + 4))) {
                            if (i == noticeData.size()-1) {
                                // 가장 최근 공지 저장하기
                                System.out.println("갱신된 최신 공지 : " + url.substring(startIndex + 4));
                                currentNotice.putString("noticeUrl", url);
                                currentNotice.putString("noticeNumber", url.substring(startIndex + 4));
                                currentNotice.apply();
                            }
                            System.out.println("공지 출력 : " + url.substring(startIndex + 4));
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            createNotificationChannel();
                            // 클릭 시 해당 공지사항 사이트로 이동
                            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
                            notificationIntent.setData(Uri.parse(notice.getUrl()));
                            PendingIntent pendingIntent;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            }
                            notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setContentTitle("[" + notice.getType() + "] " + notice.getTitle())
		                            .setContentText("클릭하여 공지사항 확인하기")
                                    .setSmallIcon(R.mipmap.ic_skup_logo)
                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                                    .setOnlyAlertOnce(true)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)
                                    .setGroup(GROUP_NAME)
		                            .setPriority(Notification.PRIORITY_HIGH)
		                            .setFullScreenIntent(pendingIntent, true)
                                    .build();
	                        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
							PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "My:notify");
	                        wakeLock.acquire(5000);
                            notificationManager.notify(notificationId++, notification);
                            j++;
                        }
                        i++;
                    }
                    if(j > 0) {
                        Notification summaryNotification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_skup_logo)
                                .setGroup(GROUP_NAME)
                                .setGroupSummary(true)
		                        .setOnlyAlertOnce(true)
		                        .setAutoCancel(true)
		                        .setPriority(Notification.PRIORITY_HIGH)
                                .build();
                        notificationManager.notify(SUMMARY_ID, summaryNotification);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.stopForever();
        thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        majorNoticeDataArrayList = new ArrayList<>();

		switch (intent.getAction()) {
			case "start":
				startService();
				break;
			case "stop":
				stopService();
				clearNotification(getApplicationContext());
				break;
		}

        return START_STICKY;
    }

	public void startService() {
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createServiceChannel();
		serviceNotification = new NotificationCompat.Builder(getApplicationContext(), SERVICE_ID)
				.setContentTitle("공지사항 알림이")
				.setContentText("어플 설정을 통해 알림이를 끌 수 있습니다.")
				.setSmallIcon(R.mipmap.ic_skup_logo)
				.setOnlyAlertOnce(true)
				.setAutoCancel(true)
				.build();

		startForeground(981215, serviceNotification);
		thread.start();
	}

	public void stopService() {
		thread.stopForever();
		stopForeground(true);
		stopSelf();
	}

    class myServiceHandler extends Handler {
		Looper looper;
		public myServiceHandler(Looper looper) {
			this.looper = looper;
		}
        @Override
        public void handleMessage(@NonNull Message msg) {
            getNotice();
        }
    }

    public void createServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    SERVICE_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setShowBadge(false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setShowBadge(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void clearNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void getNotice() {
        NoticeClient noticeClient = new NoticeClient();
        noticeApi = noticeClient.getNoticeApi();
        ArrayList<NoticeData> noticeDataArrayList = new ArrayList<>();
        noticeApi.getNotice().enqueue(new Callback<Document>() {
            @Override
            public void onResponse(Call<Document> call, Response<Document> response) {
                if (response.isSuccessful()) {
                    Document document = response.body();
                    if(document != null) {
                        Elements noticeList = document.select(".bg1");
                        noticeList.addAll(document.select(".bg2"));

                        for (Element e : noticeList) {
                            NoticeData noticeData = new NoticeData();
                            noticeData.setTitle(e.select(".title").text().substring(3));
                            noticeData.setDate(e.select(".date").text());
                            noticeData.setDepartment(e.select(".author").text());
                            noticeData.setType(e.select(".category").text());
                            noticeData.setNumber(Integer.parseInt(e.select(".num").text()));
                            noticeData.setUrl(e.select(".title").select("a").attr("href"));
                            noticeDataArrayList.add(noticeData);
                        }
                        noticeDataArrayList.sort(new Comparator<NoticeData>() {
                            @Override
                            public int compare(NoticeData noticeData, NoticeData t1) {
                                int result = -1;
                                if(noticeData.getNumber() >= t1.getNumber())
                                    result = 1;
                                return result;
                            }
                        });
                        noticeDataLiveData.setValue(noticeDataArrayList);
                    }
                }
            }

            @Override
            public void onFailure(Call<Document> call, Throwable t) {

            }
        });
    }
}