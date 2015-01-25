package com.sh4dov.carcosts.infrastructure.importers;

import com.sh4dov.carcosts.repositories.DbHandler;

import java.io.Reader;

public interface ImporterFactory {
    ImporterBase create(DbHandler dbHandler, Reader reader);
}
