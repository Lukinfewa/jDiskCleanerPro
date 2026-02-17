package com.jdisk.service;

import com.jdisk.exception.DiskCleanerException;
import java.nio.file.Path;

public class DiskService {
    private Path baseRoot;

    public DiskService(Path root) {
        this.baseRoot = root;
    }

    // Aquí irán los métodos: analizar(), limpiarTemporales(), etc.
}