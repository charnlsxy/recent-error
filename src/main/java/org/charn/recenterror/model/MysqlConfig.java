package org.charn.recenterror.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class MysqlConfig {

    @NotEmpty
    private String host;
    @NotEmpty
    private String username;
    @NotEmpty
    private String database;
    @NotEmpty
    private String password;

    private Integer port;
}
