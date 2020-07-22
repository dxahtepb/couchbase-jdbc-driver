package com.intellij;

import com.couchbase.client.java.query.QueryScanConsistency;

import java.sql.DriverPropertyInfo;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DriverPropertyInfoHelper {
  public static final String ENABLE_SSL = "sslenabled";
  public static final String ENABLE_SSL_DEFAULT = "false";
  private static final String[] BOOL_CHOICES = new String[]{"true", "false"};

  public static final String USER = "user";
  public static final String PASSWORD = "password";

  public static final String META_SAMPLING_SIZE = "meta.sampling.size";
  public static final int META_SAMPLING_SIZE_DEFAULT = 1000;


  public static DriverPropertyInfo[] getPropertyInfo() {
    ArrayList<DriverPropertyInfo> propInfos = new ArrayList<>();

    addPropInfo(propInfos, ENABLE_SSL, ENABLE_SSL_DEFAULT, "Enable ssl.", BOOL_CHOICES);
    addPropInfo(propInfos, USER, "", "Username.", null);
    addPropInfo(propInfos, PASSWORD, "", "Password.", null);
    addPropInfo(propInfos, META_SAMPLING_SIZE, Integer.toString(META_SAMPLING_SIZE_DEFAULT),
            "Number of documents that will be fetched per collection in order " +
            "to return meta information from DatabaseMetaData.getColumns method.", null);
    addPropInfo(propInfos, ScanConsistency.QUERY_SCAN_CONSISTENCY,
            ScanConsistency.QUERY_SCAN_CONSISTENCY_DEFAULT.toString(),
            "Query scan consistency.",
            ScanConsistency.CHOICES);

    return propInfos.toArray(new DriverPropertyInfo[0]);
  }

  private static void addPropInfo(final ArrayList<DriverPropertyInfo> propInfos, final String propName,
                                  final String defaultVal, final String description, final String[] choices) {
    DriverPropertyInfo newProp = new DriverPropertyInfo(propName, defaultVal);
    newProp.description = description;
    if (choices != null) {
      newProp.choices = choices;
    }
    propInfos.add(newProp);
  }

  public static boolean isTrue(String value) {
    return value != null && (value.equals("1") || value.toLowerCase(Locale.ENGLISH).equals("true"));
  }

  public static class ScanConsistency {
    private ScanConsistency() {
      // empty
    }

    public static final String QUERY_SCAN_CONSISTENCY = "query.scan.consistency";
    public static final QueryScanConsistency QUERY_SCAN_CONSISTENCY_DEFAULT = QueryScanConsistency.NOT_BOUNDED;
    private static final Map<String, QueryScanConsistency> MAPPING = Collections.unmodifiableMap(Stream
            .of(new AbstractMap.SimpleEntry<>("not_bounded", QueryScanConsistency.NOT_BOUNDED),
                new AbstractMap.SimpleEntry<>("request_plus", QueryScanConsistency.REQUEST_PLUS))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    private static final String[] CHOICES = MAPPING.keySet().toArray(new String[0]);

    public static QueryScanConsistency getQueryScanConsistency(Properties properties) {
      return getQueryScanConsistency(properties.getProperty(QUERY_SCAN_CONSISTENCY));
    }

    public static QueryScanConsistency getQueryScanConsistency(String scanConsistency) {
      if (scanConsistency == null) {
        return QUERY_SCAN_CONSISTENCY_DEFAULT;
      }
      return MAPPING.getOrDefault(scanConsistency, QUERY_SCAN_CONSISTENCY_DEFAULT);
    }
  }
}
