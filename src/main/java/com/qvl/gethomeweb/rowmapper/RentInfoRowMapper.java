package com.qvl.gethomeweb.rowmapper;

import com.qvl.gethomeweb.model.RentInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RentInfoRowMapper implements RowMapper<RentInfo> {
    @Override
    public RentInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        RentInfo rentInfo = new RentInfo();
      rentInfo.setRentInfoId(rs.getInt("rent_info_id"));
      rentInfo.setPaymentId(rs.getString("payment_id"));
      rentInfo.setTransactionId(rs.getString("transaction_id"));
      rentInfo.setRentId(rs.getInt("rent_id"));
      rentInfo.setHouseId(rs.getInt("house_id"));
      rentInfo.setAmount(rs.getInt("amount"));
      rentInfo.setMonth(rs.getInt("month"));
      rentInfo.setCreateDate(rs.getDate("created_date"));
      rentInfo.setLastUpdateDate(rs.getDate("last_update_date"));

      rentInfo.setHouseName(rs.getString("house_name"));
      rentInfo.setImageUrl(rs.getString("image_url"));
        return rentInfo;
    }
}
