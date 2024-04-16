package edu.tongji.backend.util;

public interface ILock {
    boolean tryLock(long timeoutSec);
    public void unlock();
}
