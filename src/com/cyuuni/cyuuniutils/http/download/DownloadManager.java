/*
 * Copyright (c) 2014-2015, CyuuniInfinite Chin-Z (lovewuchin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyuuni.cyuuniutils.http.download;

import android.os.Environment;
import android.text.TextUtils;

import com.cyuuni.cyuuniutils.CyHttp;
import com.cyuuni.cyuuniutils.http.HttpHandler;
import com.cyuuni.cyuuniutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author: Cyuuni
 * @since: 2015-02-09
 */
public class DownloadManager extends Thread {
    private static final int MAX_handler_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;
    private static final String FILE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Cyuuni";
    private static DownloadManager downloadManager;
    private List<CyHttp> mDownloadinghandlers;
    private List<CyHttp> mPausinghandlers;
    private HttpQueue queue;
    private Queue<String> urlQueue;
    private CyHttp http;
    private RequestCallBack<File> callBack;
    private String rootPath;
    private boolean isRunning = false;

    public DownloadManager() {
        this(FILE_ROOT);
    }
    public DownloadManager(String rootPath) {
        this.rootPath = rootPath;
        http = new CyHttp();
        queue = new HttpQueue();
        urlQueue = new LinkedList<String>();
        mDownloadinghandlers = new LinkedList<CyHttp>();
        mPausinghandlers = new LinkedList<CyHttp>();
    }

    public static DownloadManager getDownloadManager() {
        return getDownloadManager(FILE_ROOT);
    }

    public static DownloadManager getDownloadManager(String rootPath) {
        if(downloadManager == null) {
            downloadManager = new DownloadManager(rootPath);
        }
        return downloadManager;
    }

    @Override
    public void run() {
        super.run();
        while (isRunning) {
            CyHttp handler = queue.poll();
            String url = urlQueue.poll();
            if(handler != null) {
                mDownloadinghandlers.add(handler);
                handler.download(url, FILE_ROOT, callBack);
            }
        }
    }

    public void addMission(String url) {
        if(getTotalhandlerCount() >= MAX_handler_COUNT) {
            if(callBack != null) {
                callBack.onFailure(new Throwable(), 0, url);
            }
            return;
        }
        if(TextUtils.isEmpty(url) || hasMission(url)) {
            return;
        }
        urlQueue.offer(url);
        addMission(new CyHttp());
    }

    public void addMission(CyHttp handler) {
        queue.offer(handler);
        if(!this.isAlive()) {
            this.startManage();
        }
    }

    private void startManage() {
        isRunning = true;
        this.start();
    }

    private boolean hasMission(String url) {
        return false;
    }

    public void setCallBack(RequestCallBack<File> callBack) {
        this.callBack = callBack;
    }

    public int getQueuehandlerCount() {
        return queue.size();
    }

    public int getDownloadinghandlerCount() {
        return mDownloadinghandlers.size();
    }

    public int getPausinghandlerCount() {
        return mPausinghandlers.size();
    }

    public int getTotalhandlerCount() {
        return getQueuehandlerCount() + getDownloadinghandlerCount() + getPausinghandlerCount();
    }

    public class HttpQueue {
        private Queue<CyHttp> handlerQueue;

        public HttpQueue() {
            handlerQueue = new LinkedList<CyHttp>();
        }

        public void offer(CyHttp handler) {
            handlerQueue.offer(handler);
        }

        public CyHttp poll() {
            CyHttp handler = null;
            while(mDownloadinghandlers.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (handler = handlerQueue.poll()) == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return handler;
        }
        
        public CyHttp get(int position) {
            if(position >= size()) {
                return null;
            }
            return ((LinkedList<CyHttp>) handlerQueue).get(position);
        }

        public int size() {
            return handlerQueue.size();
        }

        public boolean remove(HttpHandler handler) {
            return handlerQueue.remove(handler);
        }
    }
}
