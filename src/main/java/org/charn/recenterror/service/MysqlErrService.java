package org.charn.recenterror.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.charn.recenterror.dao.SqlMapper;
import org.charn.recenterror.model.*;
import org.charn.recenterror.model.db.DbConn;
import org.charn.recenterror.model.db.Sql;
import org.charn.recenterror.model.db.SqlArg;
import org.charn.recenterror.model.db.SqlHistory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MysqlErrService {

    @Autowired
    SqlMapper mapper;

    public SqlSession openSession(DbConn conn){
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(String.format("jdbc:mysql://%s:%s/%s?characterEncoding=utf-8", conn.getHost(), 3306, conn.getDatabase()));
        dataSource.setUser(conn.getUsername());
        dataSource.setPassword(conn.getPassword());
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("runtime", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        return sqlSessionFactory.openSession();
    }

    public boolean testConn(MysqlConfig config) throws Exception {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%s/%s", config.getHost(), config.getPort(), config.getDatabase()),
                    config.getUsername(),
                    config.getPassword());
        }finally {
            if (conn != null){
                conn.close();
            }
        }
        return true;
    }

    public <T> List<T> select(String sql, Map<String, Object>  param, Class<T> clazz, DbConn conn) {
        String id = sql.hashCode() + "";
        SqlSession session = openSession(conn);
        checkStatement(sql, id, clazz, session.getConfiguration());

        try {
            return session.selectList(id, param);
        }finally {
            session.close();
        }
    }

    private void checkStatement(String sql, String id, Class<?> clazz, Configuration configuration){
        if (configuration.hasStatement(id, false)){
            return;
        }
        SqlSource source = new RawLanguageDriver().createSqlSource(configuration,
                sql, Map.class);
        ResultMap inlineResultMap = new ResultMap.Builder(configuration, id, clazz, new ArrayList<>(), null).build();
        MappedStatement statement = new MappedStatement.Builder(configuration, id, source, SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(inlineResultMap))
                .build();
        configuration.addMappedStatement(statement);
    }

    public Sql findErrBy(Integer errId) {
        return mapper.findById(errId);
    }

    public List<Map> exec(Integer id, List<SqlArg> argList){
        Sql sql = mapper.findById(id);
        DbConn dbConn = mapper.findConnById(sql.getDbConnId());
        return exec(sql, argList, dbConn);
    }

    public List<Map> exec(Sql sql, List<SqlArg> argList, DbConn conn){
        Map<String, Object> args = getArgsFrom(argList);
        saveHistory(sql, args, conn);
        return select(sql.getSql(), args, Map.class, conn);
    }

    private void saveHistory(Sql sql, Map<String, Object> args, DbConn conn) {
        SqlHistory history = new SqlHistory();

        Map<String, Object> map = new HashMap<>(3);
        map.put("sql", sql);
        map.put("args", args);
        map.put("conn", conn);

        try {
            history.setContent(new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException e) {
            log.error("save history err! content:{}, msg:{}", map, e);
        }
        history.setTime(new Date());

        mapper.saveHistory(history);
    }

    private Map<String, Object> getArgsFrom(List<SqlArg> argList) {
        if (CollectionUtils.isEmpty(argList)){
            return Collections.emptyMap();
        }
        return argList.stream().collect(Collectors.toMap(SqlArg::getArgName, SqlArg::getArgVal));
    }

    public void saveConn(String name, MysqlConfig config) {
        DbConn conn = new DbConn();
        BeanUtils.copyProperties(config, conn);
        conn.setName(name);
        mapper.saveConn(conn);
    }

    public List<DbConn> allConn() {
        return mapper.findAllConn();
    }

    public List<Sql> allSql() {
        return mapper.findAll();
    }

    @Transactional
    public Sql saveMysqlQuery(Sql param) {
        mapper.saveSql(param);
        if (!CollectionUtils.isEmpty(param.getArgList())){
            param.getArgList().forEach(e -> e.setSqlId(param.getId()));
            mapper.saveArgList(param.getArgList());
        }
        return param;
    }

    public List<SqlArg> sqlArg(Integer id) {
        return mapper.findArgBySqlId(id);
    }

    public List<SqlHistory> history() {
        return mapper.allHistory();
    }

    public void deleteAllHistory() {
        mapper.deleteAllHistory();
    }

    @Data
    public static class City{
        private Integer id;
        private String age;
        private String name;
    }
}
