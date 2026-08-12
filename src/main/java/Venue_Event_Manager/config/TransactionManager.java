package Venue_Event_Manager.config;

import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public class TransactionManager {

    private static volatile TransactionManager instance;

    private final HikariDataSource dataSource;

    public TransactionManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Singleton implementation
     * @return instance of transactionManager
     * @throws IOException
     */
    public static TransactionManager getInstance() {
        if (instance == null) {
            synchronized (TransactionManager.class) {
                if (instance == null) {
                    instance = new TransactionManager(DataSourceSingleton.getInstance());
                }
            }
        }
        return instance;
    }

    /**
     * Execute-around design pattern used for executing transactions
     * @param work lambda function with Connection as input and T as output
     * @return result of query
     * @throws TransactionException
     * @param <T>
     */
    public <T> T inTransaction(Function<Connection,T> work){
        try (Connection conn = dataSource.getConnection()){
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try{
                T result = work.apply(conn);
                conn.commit();
                return result;
            }catch (RuntimeException | Error e){
                safeRollback(conn);
                throw e;
            }catch (Exception e){
                safeRollback(conn);
                throw new TransactionException("Checked error in read-write transaction",e);
            } finally {
                safeSetAutoCommit(conn,oldAutoCommit);
            }

        }catch (SQLException e){
            throw new TransactionException("Couldn't open connection or handle transaction",e);
        }

    }

    /**
     * Execute-around design pattern used to handle read only transactions
     * @param work lambda function with Connection as input and T as output
     * @return T object
     * @param <T>
     */
    public <T> T inReadOnly(Function<Connection,T> work){

        try(Connection conn = dataSource.getConnection()){

            boolean oldAutoCommit = conn.getAutoCommit();
            boolean oldReadOnly = conn.isReadOnly();

            conn.setReadOnly(true);
            conn.setAutoCommit(false);

            try{
                T result = work.apply(conn);
                conn.rollback();
                return result;
            }catch(RuntimeException | Error e){
                safeRollback(conn);
                throw e;
            }catch (Exception e){
                safeRollback(conn);
                throw new TransactionException("Checked error in read-only transaction",e);
            }finally {
                safeSetAutoCommit(conn,oldAutoCommit);
                safeSetReadOnly(conn,oldReadOnly);
            }
        }catch (SQLException e){
            throw new TransactionException("Couldn't open connection or handle read-only transaction",e);
        }

    }

    /**
     * Executes a rollback avoiding problems due to new possible exceptions covering original problem
     * @param conn
     */
    private static void safeRollback(Connection conn){
        try {
            conn.rollback();
        } catch (SQLException e) {
        }
    }

    /**
     * Sets autoCommit option avoiding problems due to new possible exceptions covering original problem
     * @param conn
     */
    private static void safeSetAutoCommit(Connection conn, boolean value){
        try {
            conn.setAutoCommit(value);
        } catch (SQLException e) {}
    }

    /**
     * Sets read only in connection to value
     * @param conn
     * @param value
     */
    private static void safeSetReadOnly(Connection conn, boolean value){
        try{
            conn.setReadOnly(value);
        }catch (SQLException e){} //ignore
    }

}
