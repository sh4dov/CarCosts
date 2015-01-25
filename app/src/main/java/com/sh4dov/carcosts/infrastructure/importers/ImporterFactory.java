package com.sh4dov.carcosts.infrastructure.importers;

import com.sh4dov.carcosts.repositories.DbHandler;

public interface ImporterFactory {
    ImporterBase create(DbHandler dbHandler);
}
