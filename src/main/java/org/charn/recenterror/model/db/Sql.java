package org.charn.recenterror.model.db;

import lombok.Data;

import java.util.List;

@Data
public class Sql extends Table{
    private String name;
    private String sql;
    private Integer dbConnId;

    private List<SqlArg> argList;
}
