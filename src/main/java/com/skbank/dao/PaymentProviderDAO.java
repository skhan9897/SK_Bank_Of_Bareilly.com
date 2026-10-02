package com.skbank.dao;

import com.skbank.model.PaymentProvider;
import java.sql.SQLException;
import java.util.List;

public interface PaymentProviderDAO {
    List<PaymentProvider> findByType(String providerType) throws SQLException;
    PaymentProvider findByCode(String code) throws SQLException;
}
