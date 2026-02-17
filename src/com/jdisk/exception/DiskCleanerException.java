package com.jdisk.exception;

public class DiskCleanerException extends Exception {
    public DiskCleanerException(String message) {
        super(message);
    }
    public DiskCleanerException(String message, Throwable cause) {
        super(message, cause);
    }
}