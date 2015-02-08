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

package com.cyuuni.cyuuniutils.http.callback;

/**
 * @author: Cyuuni
 * @since: 2015-02-08
 */
public abstract class RequestCallBack<T> {
	
	private boolean progress = true;
	private int rate = 1000 * 1;//每秒

    public RequestCallBack() {

    }

    public RequestCallBack(int rate) {
        this.rate = rate;
    }

    /**
     * @param progress
     * @param rate
     */
    public RequestCallBack(boolean progress , int rate) {
        this.progress = progress;
        this.rate = rate;
    }

	public boolean isProgress() {
		return progress;
	}
	
	public int getRate() {
		return rate;
	}
	
	public void onStart(){};
	public void onLoading(long count,long current){};
	public abstract void onSuccess(T response);
	public abstract void onFailure(Throwable t,int errorNo ,String strMsg);
}
