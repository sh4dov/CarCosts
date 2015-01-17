package com.sh4dov.carcosts.infrastructure.exporters;

import com.sh4dov.carcosts.repositories.DbHandler;

public interface ExporterFactory {
    ExporterBase create(DbHandler dbHandler);
}
