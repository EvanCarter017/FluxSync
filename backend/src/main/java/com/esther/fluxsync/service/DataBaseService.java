package com.esther.fluxsync.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DataBaseService {

    private final JdbcTemplate jdbcTemplate;

    public DataBaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 通用查询
    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapper.map(rs), params);
    }

    // 单返回查询
    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = query(sql, mapper, params);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    // 更新操作
    public int update(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }
}
