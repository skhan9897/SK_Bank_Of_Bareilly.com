package com.skbank.dao.impl;

import com.skbank.dao.PaymentProviderDAO;
import com.skbank.model.PaymentProvider;
import com.skbank.model.ProviderType;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentProviderDAOImpl implements PaymentProviderDAO {

    @Override
    public List<PaymentProvider> findByType(String providerType) throws SQLException {
        List<PaymentProvider> list = new ArrayList<>();
        String sql = "SELECT * FROM payment_providers WHERE provider_type = ? AND status = 'ACTIVE' ORDER BY provider_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, providerType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProvider(rs));
            }
        }
        return list;
    }

    @Override
    public PaymentProvider findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM payment_providers WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProvider(rs);
            }
        }
        return null;
    }

    private PaymentProvider mapProvider(ResultSet rs) throws SQLException {
        PaymentProvider p = new PaymentProvider();
        p.setProviderId(rs.getLong("provider_id"));
        p.setProviderType(ProviderType.valueOf(rs.getString("provider_type")));
        p.setProviderName(rs.getString("provider_name"));
        p.setCode(rs.getString("code"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
