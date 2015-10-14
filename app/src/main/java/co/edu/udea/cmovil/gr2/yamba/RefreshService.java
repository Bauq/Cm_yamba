package co.edu.udea.cmovil.gr2.yamba;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;
import com.thenewcircle.yamba.client.YambaStatus;

import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RefreshService extends IntentService {
    public static final String ACTION_FOO = "co.edu.udea.cmovil.gr2.yamba.action.FOO";
    public static final String ACTION_BAZ = "co.edu.udea.cmovil.gr2.yamba.action.BAZ";

    public static final String EXTRA_PARAM1 = "co.edu.udea.cmovil.gr2.yamba.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "co.edu.udea.cmovil.gr2.yamba.extra.PARAM2";

    private static final String TAG = RefreshService.class.getSimpleName();
    public boolean isEmpty;

    public RefreshService() {
        super("RefreshService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onStarted");
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this); //*
        final String username = prefs.getString("username", ""); //*
        final String password = prefs.getString("password", ""); //*

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            isEmpty = true;
            return;
        } //Verificar que no hayan campos vacíos

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        YambaClient cloud = new YambaClient(username, password); /*Se crea un nuevo
    cliente yamba*/
        try {
            List<YambaStatus> timeline = cloud.getTimeline(20); /* Obtener linea de
       tiempo, los últimos 20 estados*/
            for (YambaStatus status : timeline) { //
                Log.d("Prueba1", String.format("%s: %s", status.getUser(), status.getMessage())); //Imprimir estados en consola

                values.clear();
                values.put(StatusContract.Column.ID, status.getId());
                values.put(StatusContract.Column.USER, status.getUser());
                values.put(StatusContract.Column.MESSAGE, status.getMessage());
                values.put(StatusContract.Column.CREATED_AT, status.getCreatedAt().getTime());
                db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            }
        } catch (YambaClientException e) { //
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }
        return;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isEmpty){
            Toast.makeText(this, "Por favor actualiza tu usuario y contraseña", Toast.LENGTH_LONG).show();
            isEmpty = false;
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
