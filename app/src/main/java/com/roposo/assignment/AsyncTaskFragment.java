package com.roposo.assignment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.roposo.assignment.data.DataManager;
import com.roposo.assignment.data.JSONDataParser;
import com.roposo.assignment.data.Story;

import java.util.List;

public class AsyncTaskFragment extends Fragment {

    // Declare some sort of interface that your AsyncTask will use to communicate with the Activity
    public interface TaskResultListener {
        void onTaskStarted();

        void onTaskFinished(List<Story> result);
    }

    private NetworkTask mTask;
    private TaskResultListener mListener;
    private boolean isRunning;

    private List<Story> mResult;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TaskResultListener) {
            mListener = (TaskResultListener) activity;
        } else {
            throw new IllegalStateException("Activity must implement TaskResultListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * The Activity can call this when it wants to start the task
     */
    public void startTask() {
        mTask = new NetworkTask();
        mTask.execute();
    }

    /**
     * Cancel currently running task.
     */
    public void cancelTask() {
        if ((mTask != null) && (mTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mTask.cancel(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // If the AsyncTask finished when we didn't have a listener we can
        // deliver the result here
        if ((mResult != null) && (mListener != null)) {
            mListener.onTaskFinished(mResult);
            mResult = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTask();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private class NetworkTask extends AsyncTask<String, Integer, List<Story>> {

        @Override
        protected void onPreExecute() {
            isRunning = true;
            if (mListener != null) {
                mListener.onTaskStarted();
            }
        }

        @Override
        protected List<Story> doInBackground(String... urls) {
            new JSONDataParser().praseData(getActivity());
            return DataManager.getInstance().getAllStory();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isRunning = true;
        }

        @Override
        protected void onPostExecute(List<Story> result) {
            if (mListener != null) {
                mListener.onTaskFinished(result);
            } else {
                mResult = result;
                isRunning = false;
            }
        }

    }
}