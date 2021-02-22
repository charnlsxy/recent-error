package org.charn.recenterror.model.db;

import lombok.Data;

@Data
public class DbConn extends Table{

    private String host;
    private String username;
    private String database;
    private String password;
    private String name;
}
