package com.hiking.infrastructure.persistence.typehandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON List 类型处理器
 * 用于将 List<String> 与数据库 JSON/VARCHAR 字段互相转换
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting List to JSON", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private List<String> parseJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new SQLException("Error parsing JSON to List", e);
        }
    }
}
