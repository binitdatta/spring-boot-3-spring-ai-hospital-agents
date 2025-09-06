package com.rollingstone.tools;


import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SqlExecutorTool {

    private static final Pattern ONLY_SELECT =
            Pattern.compile("^\\s*SELECT\\s+.+", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private final NamedParameterJdbcTemplate jdbc;

    public SqlExecutorTool(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record Result(String sql, Map<String,Object> params, List<Map<String,Object>> rows, int rowCount, long ms) {}

    public Result runReadOnly(String sql, Map<String,Object> params, int hardLimit) {

        sql = stripCatalogPrefixes(sql);

        // 1) Validate
        if (sql.contains(";")) throw new IllegalArgumentException("Multiple statements not allowed.");
        if (!ONLY_SELECT.matcher(sql).matches()) throw new IllegalArgumentException("Only SELECT is allowed.");
        if (sql.matches("(?i).*\\b(UPDATE|INSERT|DELETE|CREATE|ALTER|DROP|TRUNCATE|MERGE|CALL|GRANT|REVOKE)\\b.*"))
            throw new IllegalArgumentException("Write/DDL keywords are not allowed.");

        // 2) Enforce LIMIT
        String limited = enforceLimit(sql, hardLimit <= 0 ? 200 : hardLimit);

        // 3) Execute
        long t0 = System.currentTimeMillis();
        List<Map<String,Object>> rows = jdbc.query(limited, params == null ? Map.of() : params, new ColumnMapRowMapper());
        long ms = System.currentTimeMillis() - t0;

        return new Result(limited, params == null ? Map.of() : params, rows, rows.size(), ms);
    }

    private String enforceLimit(String sql, int limit) {
        // naive: if there's no LIMIT, append one; if there is, keep the smaller
        if (!sql.matches("(?i).*\\bLIMIT\\s+\\d+\\b.*")) {
            return sql + " LIMIT " + limit;
        }
        // If present, do not increase the limit; you could also lower it.
        return sql;
    }

    private static final Set<String> TABLES = Set.of(
            "department","unit","room","bed","staff_role","staff","staff_role_map",
            "patient","encounter","evs_ticket","transport_job",
            "inventory_item","unit_inventory","rag_document","rag_chunk","rag_embedding");

    private String stripCatalogPrefixes(String sql) {
        // Replace things like hospital.bed -> bed for known tables
        String out = sql;
        for (String t : TABLES) {
            out = out.replaceAll("(?i)\\b[\\w$]+\\." + t + "\\b", t);
        }
        return out;
    }
}
