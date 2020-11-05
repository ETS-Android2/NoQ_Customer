package com.younoq.noq.models;

import android.content.Context;
import android.util.Log;

import com.snatik.storage.Storage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Harsh Chaurasia(Phantom Boy) on 10/06/2020.
 */

public class Logger {

    private Context context;
    private Storage storage;
    private String internalFilePath, logFileName, logTime, phone;
    private final String TAG = "Logger";
    private Date date;
    private SimpleDateFormat dateFormat, timeFormat;
    private SaveInfoLocally saveInfoLocally;

    public Logger(Context ctx){
        this.context = ctx;
        this.storage = new Storage(context);
        date = new Date();
        saveInfoLocally = new SaveInfoLocally(ctx);
        phone = saveInfoLocally.getPhone();
        phone = phone.replace("+91", "");
        dateFormat = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        logFileName = phone + "_" + dateFormat.format(date) + "NoQ.log";
    }

    public void writeLog(String TAG, String func, String logs) {

        // Getting the Time of the Log.
        date = new Date();
        logTime = timeFormat.format(date);

        final int pid = android.os.Process.myPid();

        // preparing the log in a specific well readable Format.
        final String content = logTime + " " +pid +"/" + TAG + "/" + func + ": " + logs;

        internalFilePath = this.storage.getInternalFilesDirectory();

        // Checking if the Logs Directory Exists or not, If not then create the Directory.
        final String logsDirPath = internalFilePath.replace("files", "") + "Logs";
        final boolean dirExists = storage.isDirectoryExists(logsDirPath);

        if(!dirExists) {
            // Creating the Logs Directory.
            storage.createDirectory(logsDirPath);
        } else {
            Log.d(TAG, "Logs/ Dir Exists");
        }

        final String logfilePath = logsDirPath + File.separator + logFileName;

        Log.d(TAG, "Internal File Path : "+ logfilePath +", LogFile Name : "+logFileName);

        // Saving the Log File Path to SharedPreferences.
        saveInfoLocally.setLogFilePath(logfilePath);

        final boolean fileExists = storage.isFileExist(logfilePath);
        if(fileExists) {

            // File Already Exists so, Appending the Logs to the Existing File.
            storage.appendFile(logfilePath, content);

        } else {

            // File Doesn't Exists, so create a new logFile.
            storage.createFile(logfilePath, content);

        }


    }

}
