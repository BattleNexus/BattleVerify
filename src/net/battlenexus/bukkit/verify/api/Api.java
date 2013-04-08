package net.battlenexus.bukkit.verify.api;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.battlenexus.bukkit.verify.sql.SqlClass;

public class Api {

    public static String version = "1.0.0";
    public static SqlClass sql;
    public static String lastVerifiedUser = null;
    
    /**
     * Confirms the user's account by the code they entered    
     * @param code
     * @param username
     * @return boolean - true if confirmed, false if code not found or already confirmed
     */
    public static boolean confirmByCode(String code, String username) {
        String[] params = { code };
        sql.build("SELECT count(verify_code) as num, verify_status, verify_fusername FROM "+sql.prefix+"verify WHERE verify_code=?");
        ResultSet results = sql.executePreparedQuery( params );
        if(results == null) return false;
        try {
            while (results.next()) {
                if (results.getInt("num") > 0){
                    if(results.getInt("verify_status") == 0){
                        lastVerifiedUser = results.getString("verify_fusername");
                        String[] p = {username, code};
                        sql.build("UPDATE "
                                + sql.prefix
                                + "verify SET verify_status=1, verify_username=? WHERE verify_code=? && verify_status=0");
                        sql.executePreparedUpdate(p);
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}