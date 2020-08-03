package com.intellij.executor;

import com.couchbase.client.core.retry.BestEffortRetryStrategy;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.DropBucketOptions;
import com.intellij.CouchbaseConnection;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.executor.CustomDdlExecutor.startsWithIgnoreCase;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

class DropBucketExecutor implements CustomDdlExecutor {
    private static final Pattern DROP_BUCKET_PATTERN = Pattern.compile(
            "^DROP\\s+BUCKET\\s+`(?<name>[0-9a-zA-Z_.%\\-]+)`\\s*;?\\s*", CASE_INSENSITIVE);

    public boolean mayAccept(@NotNull String sql) {
        return startsWithIgnoreCase(sql, "DROP BUCKET");
    }

    public ExecutionResult execute(@NotNull CouchbaseConnection connection, @NotNull String sql) {
        Matcher matcher = DROP_BUCKET_PATTERN.matcher(sql);
        if (matcher.matches()) {
            Cluster cluster = connection.getCluster();
            String name = matcher.group("name");
            if (cluster.buckets().getAllBuckets().containsKey(name)) {
                cluster.buckets().dropBucket(name, DropBucketOptions.dropBucketOptions()
                        .retryStrategy(BestEffortRetryStrategy.INSTANCE));
            }
            return new ExecutionResult(true);
        }
        return new ExecutionResult(false);
    }

    @Override
    public boolean isRequireWriteAccess() {
        return true;
    }
}
