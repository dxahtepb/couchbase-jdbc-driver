package com.intellij;

import com.intellij.executor.ExecutionResult;
import com.intellij.resultset.CouchbaseListResultSet;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import static com.intellij.executor.CouchbaseCustomStatementExecutor.tryExecuteDdlStatement;
import static java.lang.Math.max;

public class CouchbaseStatement extends CouchbaseBaseStatement {
    CouchbaseStatement(@NotNull CouchbaseConnection connection) {
        super(connection);
    }

    @Override
    public ResultSet executeQuery(@NotNull String sql) throws SQLException {
        checkClosed();
        execute(sql);
        if (result == null) {
            throw new SQLException("No result set");
        }
        return result;
    }

    CouchbaseListResultSet executeMetaQuery(@NotNull String sql) throws SQLException {
        checkClosed();
        try {
            return new CouchbaseListResultSet(cluster.query(sql, makeQueryOptions()));
        } catch (Throwable t) {
            throw new SQLException(t.getMessage(), t);
        }
    }

    @Override
    public int executeUpdate(@NotNull String sql) throws SQLException {
        checkClosed();
        execute(sql);
        return max(0, getUpdateCount());
    }

    @Override
    public boolean execute(@NotNull String sql) throws SQLException {
        checkClosed();
        try {
            ExecutionResult executionResult = tryExecuteDdlStatement(connection, sql);
            if (executionResult.isSuccess()) {
                ResultSet resultSet = executionResult.getResultSet();
                setNewResultSet(resultSet);
                return resultSet != null;
            }
            return executeInner(sql, cluster.reactive().query(sql, makeQueryOptions()));
        } catch (Throwable t) {
            throw new SQLException(t);
        }
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
