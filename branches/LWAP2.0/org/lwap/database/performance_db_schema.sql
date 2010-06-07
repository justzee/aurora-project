    CREATE TABLE FND_SQL_STATS (
      OWNER                      VARCHAR2(300) NOT NULL,
      SQL                        VARCHAR2(4000) NOT NULL,
      DATE_FROM                  DATE DEFAULT SYSDATE NOT NULL,
      DATE_TO                    DATE NOT NULL,
      TOTAL_EXEC_TIME            NUMBER,
      TOTAL_EXEC_COUNT           NUMBER,
      AVG_EXEC_TIME              NUMBER,
      MAX_EXEC_TIME              NUMBER,
      MIN_EXEC_TIME              NUMBER    
    );
    
    CREATE INDEX FND_SQL_STATS_IDX1 ON FND_SQL_STATS(OWNER);
    CREATE INDEX FND_SQL_STATS_IDX2 ON FND_SQL_STATS(DATE_FROM);