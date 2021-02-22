package org.charn.recenterror.controller;

import org.charn.recenterror.exception.ServerException;
import org.charn.recenterror.model.*;
import org.charn.recenterror.model.db.DbConn;
import org.charn.recenterror.model.db.Sql;
import org.charn.recenterror.model.db.SqlArg;
import org.charn.recenterror.model.db.SqlHistory;
import org.charn.recenterror.service.MysqlErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/err/mysql")
public class IndexController {

    @Autowired
    MysqlErrService mysqlErrService;

    @RequestMapping
    public Sql mysqlErr(@NotNull @Min(value = 0L, message = "错误信息id") Integer errId){
        return mysqlErrService.findErrBy(errId);
    }

    @RequestMapping("/exec")
    public List<Map> execSql(@NotNull @Min(value = 0L, message = "错误信息id") Integer id,
                             @RequestBody List<SqlArg> argList){
        return mysqlErrService.exec(id, argList);
    }

    @GetMapping("/history")
    public List<SqlHistory> history(){
        return mysqlErrService.history();
    }

    @DeleteMapping("/history")
    public void deleteHistory(){
        mysqlErrService.deleteAllHistory();
    }

    @RequestMapping("/test")
    public void mysqlTestConn(@Valid MysqlConfig config){
        if (config.getPort() == null){
            config.setPort(3306);
        }
        try {
            mysqlErrService.testConn(config);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }

    @RequestMapping("/saveConn")
    public DbConn saveDbConn(@NotEmpty String name, @Valid @RequestBody MysqlConfig config){
        return mysqlErrService.saveConn(name, config);
    }

    @RequestMapping("/allConn")
    public List<DbConn> findAllExistDb(){
        return mysqlErrService.allConn();
    }

    @RequestMapping("/allSql")
    public List<Sql> findAllSql(){
        return mysqlErrService.allSql();
    }

    @RequestMapping("/sqlArg")
    public List<SqlArg> sqlArg(Integer sqlId){
        return mysqlErrService.sqlArg(sqlId);
    }

    @RequestMapping("/saveSql")
    public Sql saveMysqlQuery(@RequestBody Sql param){
        return mysqlErrService.saveMysqlQuery(param);
    }

}
