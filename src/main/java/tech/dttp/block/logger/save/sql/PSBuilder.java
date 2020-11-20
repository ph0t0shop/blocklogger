package tech.dttp.block.logger.save.sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PSBuilder {
    private Connection conn;
    private String baseQuery;
    private HashMap<String, PSPredicate> predicates = new HashMap<>();
    private PreparedStatement ps;

    public PSBuilder(Connection conn, String baseQuery) {
        this.conn = conn;
        this.baseQuery = baseQuery;
    }

    public void addPredicate(SQLType type, String name, String predicate, int numArgs) {
        predicates.put(name, new PSPredicate(predicate, numArgs, type));
    }

    public void addPredicate(SQLType type, String name, String predicate) {
        this.addPredicate(type, name, predicate, (int) predicate.chars().filter(c -> c == '?').count());
    }

    public void prepare () throws SQLException {
        StringBuilder query = new StringBuilder(baseQuery);
        query.append(' ');
        boolean first = true;

        for (String param : params()) {
            PSPredicate predicate = predicates.get(param);
            if (first) {
                first = false;
            } else {
                query.append(" AND ");
            }
            query.append("((").append(predicate.predicate).append(") OR ?)");
        }
        ps = conn.prepareStatement(query.toString());
    }

    public Runner createRunner() {
        return new Runner();
    }

    private ArrayList<String> params() {
        ArrayList<String> params = new ArrayList<>(predicates.keySet());
        params.sort(String::compareTo);
        return params;
    }

    public PreparedStatement getStatement() {
        return ps;
    }

    public class Runner {
        private HashMap<String, Object[]> paramValues = new HashMap<>();

        public Runner() {
        }

        public void fillParameter (String name, Object... value) {
            this.paramValues.put(name, value);
        }

        public ResultSet execute() throws SQLException { // TODO: thread safety
            int i = 1; // 1-indexed, ugh
            ps.clearParameters();
            for (String param : params()) {
                PSPredicate predicate = predicates.get(param);
                Object[] args = paramValues.get(param);
                for (int j = 0; j < predicate.numArgs; j++) {
                    if (args != null) {
                        ps.setObject(i, args[j % args.length], predicate.type.getVendorTypeNumber());
                    } else {
                        setDefaultVal(ps, i, predicate.type);
                    }
                    i++;
                }
                ps.setBoolean(i, args == null);
                i++;
            }
            return ps.executeQuery();
        }

        private void setDefaultVal(PreparedStatement ps, int i, SQLType type) throws SQLException {
            // ps.setNull(i, type.getVendorTypeNumber());
            switch (type.getVendorTypeNumber()) {
                case Types.NCHAR:
                case Types.NVARCHAR:
                case Types.VARCHAR:
                    ps.setString(i, "");
                    break;
                default:
                    ps.setNull(i, type.getVendorTypeNumber());
                    break;
            }
        }
    }


    private static class PSPredicate {
        private String predicate;
        private int numArgs;
        private SQLType type;

        public PSPredicate(String predicate, int numArgs, SQLType type) {
            this.predicate = predicate;
            this.numArgs = numArgs;
            this.type = type;
        }
    }
}
