package org.charn.recenterror.dao;

import org.apache.ibatis.annotations.Mapper;
import org.charn.recenterror.model.db.DbConn;
import org.charn.recenterror.model.db.Sql;
import org.charn.recenterror.model.db.SqlArg;
import org.charn.recenterror.model.db.SqlHistory;

import java.util.List;

/**
 * sql table sql_arg
 */
@Mapper
public interface SqlMapper {

    Sql findById(Integer id);

    List<Sql> findAll();

    int saveConn(DbConn conn);

    List<DbConn> findAllConn();

    DbConn findConnById(Integer id);

    int saveArgList(List<SqlArg> argList);

    List<SqlArg> findArgBySqlId(Integer id);

    int saveSql(Sql sql);

    int saveHistory(SqlHistory history);

    List<SqlHistory> allHistory();

    void deleteAllHistory();
}
