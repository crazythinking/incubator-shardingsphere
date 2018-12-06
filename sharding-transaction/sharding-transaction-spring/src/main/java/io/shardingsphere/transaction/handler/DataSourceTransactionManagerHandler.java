/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingsphere.transaction.handler;

import io.shardingsphere.core.exception.ShardingException;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Datasource transaction manager handler.
 *
 * @author yangyi
 */

public final class DataSourceTransactionManagerHandler extends AbstractTransactionManagerHandler {
    
    private final DataSourceTransactionManager transactionManager;
    
    public DataSourceTransactionManagerHandler(final PlatformTransactionManager transactionManager) {
        this.transactionManager = (DataSourceTransactionManager) transactionManager;
    }
    
    @Override
    public void unbindResource() {
        ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.unbindResource(transactionManager.getDataSource());
        DataSourceUtils.releaseConnection(holder.getConnection(), transactionManager.getDataSource());
    }
    
    @Override
    protected Connection getConnectionFromTransactionManager() {
        Connection result = null;
        try {
            result = transactionManager.getDataSource().getConnection();
            TransactionSynchronizationManager.bindResource(transactionManager.getDataSource(), new ConnectionHolder(result));
        } catch (SQLException e) {
            throw new ShardingException("Could not open JDBC Connection before transaction", e);
        }
        return result;
    }
}