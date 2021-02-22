package org.charn.recenterror.model.db;


import lombok.Data;

@Data
public class SqlArg extends Table{
    private Integer sqlId;
    private String argName;
    private Integer type;
    private String argVal;
}

