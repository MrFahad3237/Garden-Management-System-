package bll.validators;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GlobalLockHelper {
    public static Lock LOCK = new ReentrantLock();
}

