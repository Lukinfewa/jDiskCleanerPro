package com.jdisk.model;

public record FileStats(String name, long size, String path) {
    @Override
    public String toString() {
        return String.format("%-30s | %10.2f MB", name, size / (1024.0 * 1024.0));
    }
}