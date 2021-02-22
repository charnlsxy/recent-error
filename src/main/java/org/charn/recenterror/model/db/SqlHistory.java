package org.charn.recenterror.model.db;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SqlHistory extends Table{

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

}
