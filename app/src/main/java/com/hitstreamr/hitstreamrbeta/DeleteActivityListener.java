package com.hitstreamr.hitstreamrbeta;

public interface DeleteActivityListener {
    /**
     * calls finish, so activity will close, even when called from a RCV
     */
    public void callFinish();
}
