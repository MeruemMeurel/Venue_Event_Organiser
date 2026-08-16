package Venue_Event_Manager.config;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public class TransactionManager {

    private static volatile TransactionManager instance;

    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Singleton implementation
     * @return instance of transactionManager
     * @throws IllegalStateException if the data source cannot be initialized
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
     * @throws TransactionException if the connection, transaction, commit, or rollback fails
     * @param <T> result type returned by the work
     */
    public <T> T inTransaction(Function<Connection,T> work){
        try (Connection conn = dataSource.getConnection()){
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            Throwable failure = null;

            try{
                T result = work.apply(conn);
                conn.commit();
                return result;
            }catch (RuntimeException | Error e){
                failure = e;
                addRollbackFailure(conn,e);
                throw e;
            }catch (SQLException e){
                TransactionException transactionFailure =
                        new TransactionException("Couldn't commit read-write transaction",e);
                failure = transactionFailure;
                addRollbackFailure(conn,transactionFailure);
                throw transactionFailure;
            }catch (Exception e){
                TransactionException transactionFailure =
                        new TransactionException("Checked error in read-write transaction",e);
                failure = transactionFailure;
                addRollbackFailure(conn,transactionFailure);
                throw transactionFailure;
            } finally {
                restoreAutoCommit(conn,oldAutoCommit,failure);
            }

        }catch (SQLException e){
            throw new TransactionException("Couldn't open connection or handle transaction",e);
        }

    }

    /**
     * Execute-around design pattern used to handle read only transactions
     * @param work lambda function with Connection as input and T as output
     * @return result produced by the work
     * @param <T> result type returned by the work
     */
    public <T> T inReadOnly(Function<Connection,T> work){

        try(Connection conn = dataSource.getConnection()){

            boolean oldAutoCommit = conn.getAutoCommit();
            boolean oldReadOnly = conn.isReadOnly();

            conn.setReadOnly(true);
            conn.setAutoCommit(false);
            Throwable failure = null;

            try{
                T result = work.apply(conn);
                conn.rollback();
                return result;
            }catch(RuntimeException | Error e){
                failure = e;
                addRollbackFailure(conn,e);
                throw e;
            }catch (SQLException e){
                TransactionException transactionFailure =
                        new TransactionException("Couldn't complete read-only transaction",e);
                failure = transactionFailure;
                addRollbackFailure(conn,transactionFailure);
                throw transactionFailure;
            }catch (Exception e){
                TransactionException transactionFailure =
                        new TransactionException("Checked error in read-only transaction",e);
                failure = transactionFailure;
                addRollbackFailure(conn,transactionFailure);
                throw transactionFailure;
            }finally {
                restoreAutoCommit(conn,oldAutoCommit,failure);
                restoreReadOnly(conn,oldReadOnly,failure);
            }
        }catch (SQLException e){
            throw new TransactionException("Couldn't open connection or handle read-only transaction",e);
        }

    }

    /**
     * Executes a rollback and preserves a possible rollback error as secondary information.
     * @param conn connection to roll back
     * @param primaryFailure original failure that triggered the rollback
     */
    private static void addRollbackFailure(Connection conn, Throwable primaryFailure){
        try {
            conn.rollback();
        } catch (SQLException rollbackFailure) {
            primaryFailure.addSuppressed(rollbackFailure);
        }
    }

    /**
     * Restores auto-commit without hiding a previous failure.
     * @param conn connection to restore
     * @param value auto-commit value to restore
     * @param primaryFailure previous transaction failure, or {@code null} after successful work
     */
    private static void restoreAutoCommit(Connection conn, boolean value, Throwable primaryFailure){
        try {
            conn.setAutoCommit(value);
        } catch (SQLException restoreFailure) {
            handleCleanupFailure("Couldn't restore auto-commit",restoreFailure,primaryFailure);
        }
    }

    /**
     * Restores the read-only flag without hiding a previous failure.
     * @param conn connection to configure
     * @param value read-only flag
     * @param primaryFailure previous transaction failure, or {@code null} after successful work
     */
    private static void restoreReadOnly(Connection conn, boolean value, Throwable primaryFailure){
        try{
            conn.setReadOnly(value);
        }catch (SQLException restoreFailure){
            handleCleanupFailure("Couldn't restore read-only state",restoreFailure,primaryFailure);
        }
    }

    /**
     * Attaches cleanup errors to an existing failure, or reports them directly after successful work.
     * @param message cleanup operation description
     * @param cleanupFailure cleanup error to preserve
     * @param primaryFailure previous transaction failure, or {@code null}
     * @throws TransactionException if cleanup failed after otherwise successful work
     */
    static void handleCleanupFailure(String message, SQLException cleanupFailure,
                                     Throwable primaryFailure){
        if(primaryFailure != null){
            primaryFailure.addSuppressed(cleanupFailure);
            return;
        }
        throw new TransactionException(message,cleanupFailure);
    }

}
