package com.haniokasai.nukkit.ArrowGun;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;

import java.sql.*;
import java.util.HashMap;


public class mysql implements Listener {

	/*決まり文句*/
    Main plugin;

    public mysql(Main plugin){
        this.plugin = plugin;
    }


	static Connection conn =null;

	public static void load(){
		try {
			  Class.forName("com.mysql.jdbc.Driver");
			 conn =
		                DriverManager.getConnection("jdbc:mysql://example.com:3306/ex?useUnicode=true&characterEncoding=UTF-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","username","passwd");
		            // ステートメントを作成
			  Server.getInstance().getLogger().info("[ArrowGun] my1");
		            Statement stmt = conn.createStatement();
		            stmt.setQueryTimeout(10);
		            Server.getInstance().getLogger().info("[ArrowGun]my 2");
		            // INSERT
		            //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS gundata (gunid INT , type VARCHAR(16) ,speed INT , sleep INT , name_jpn VARCHAR(20) ,name_eng VARCHAR(20))");
		            Server.getInstance().getLogger().info("[ArrowGun] my3");


		            // ステートメントをクローズ
		            stmt.close();
		Server.getInstance().getScheduler().scheduleRepeatingTask(new Runnable() {
					@Override
					public void run() {
						mysql.reload();
						}
				},20*30);
		}catch (ClassNotFoundException e){
			Server.getInstance().getLogger().info("[ArrowGun] ClassNotFoundException"+e);
		}catch (Exception e){
			Server.getInstance().getLogger().info("[ArrowGun] Exception"+e);
		}
	}

	public static void shutdown(){
        try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
     public static boolean reload(){
         try {
			conn.close();
			conn = DriverManager.getConnection("jdbc:mysql://example.com:3306/ex?useUnicode=true&characterEncoding=UTF-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","username","passwd");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
         return true;
    }


	public static void cre8(String name){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT name FROM honey WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      String r = null;
		      while(rs.next()){
		         r = rs.getString("name");
		      }
		      if(r == null){
		    	  stmt.executeUpdate("INSERT INTO honey (name, ap1,kill1,renkill1,money1,des1,gun1,gun2,gun3,gun4,gun5,gun6,gun7,gun8,gun9,gun10,flogin,llogin,hashcode,skill1,gslot1,isvip,rank,region) VALUES ('"+name+"',0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,"+(int) (System.currentTimeMillis()/1000)+","+(int) (System.currentTimeMillis()/1000)+",0,0,3,0,0,"+lj.conf_region+");");
		      }
		      rs.close();

		    	  stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public static int get(String name,String kind){
		try {
			Statement stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT * FROM honey WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      int r = 0;
		      while(rs.next()){
		         r = rs.getInt(kind);
		      }
		      rs.close();
		      stmt.close();
		      return r;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean set(String name,String kind,int con){
		try {
			Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE honey SET  "+kind+" = "+con+"  WHERE name = '"+name+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean add(String name,String kind,int con){
		try {
			Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE honey SET  "+kind+" = "+kind+" + "+con+"  WHERE name = '"+name+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean addap(String name,int ap){
		try {
			Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE honey SET  ap1 = ap1 + "+ap+" ,money1 = money1 + "+ap+"  WHERE name = '"+name+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void setname(String name,String rank){
		try {
			PreparedStatement pstmt = conn.prepareStatement("UPDATE honey SET  rank = ?  WHERE name = ?");
			pstmt.setString(1, rank);
			pstmt.setString(2, name);
			ResultSet rs = pstmt.executeQuery();
		    rs.close();
		    pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getname(String name){
		String r = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT rank FROM honey WHERE name = '"+name+"'");

		      while(rs.next()){
		         r = rs.getString("rank");
		      }

		      rs.close();
		      stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}


	public static int getregion(String name){
		int r = 1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT region FROM honey WHERE name = '"+name+"'");

		      while(rs.next()){
		         r = rs.getInt("region");
		      }

		      rs.close();
		      stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}

	public static boolean canchat(String name){
		int r = 1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT chat FROM honey WHERE name = '"+name+"'");

		      while(rs.next()){
		         r = rs.getInt("chat");
		      }

		      rs.close();
		      stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(r == 2){
			return true;
		}else{
			return false;
		}
	}

	public static boolean killed (String killer,String death){
		try {
				Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE honey SET kill1 = kill1 + 1,renkill1 = renkill1+1 WHERE name = '"+killer+"'");
		      stmt.executeUpdate("UPDATE honey SET des1 = des1 +1,renkill1 = 0 WHERE name = '"+death+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static HashMap<String, Integer> getskills(String name){
		try {
			Statement stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT * FROM honey WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      HashMap<String,Integer> map = new HashMap<String,Integer>();
		      while(rs.next()){
		         map.put("skill", rs.getInt("skill1"));
		         map.put("gun1", rs.getInt("gun1"));
		         map.put("gun2", rs.getInt("gun2"));
		         map.put("gun3", rs.getInt("gun3"));
		         map.put("gun4", rs.getInt("gun4"));
		         map.put("gun5", rs.getInt("gun5"));
		         map.put("gun6", rs.getInt("gun6"));
		         map.put("gun7", rs.getInt("gun7"));
		         map.put("gun8", rs.getInt("gun8"));
		         map.put("gun9", rs.getInt("gun9"));
		         map.put("gun10", rs.getInt("gun10"));
		         map.put("gslot", rs.getInt("gslot1"));
		         map.put("money", rs.getInt("money1"));
		         map.put("kill", rs.getInt("kill1"));
		      }
		      rs.close();
		      stmt.close();
		      return map;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean usemoney(String name,int amount){
		try {
			Statement stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT money1,isvip FROM honey WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      int mo =0;
		      int v =0;
		      while(rs.next()){
		         mo = rs.getInt("money1");
		         v = rs.getInt("isvip");
		      }
		      rs.close();
		      stmt.close();
		      if(v > 0){
		    	  return true;
		      }else if(mo >= amount){
		    	  add(name,"money1",-amount);
		    	  return true;
		      }else{
		    	  return false;
		      }
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isvip(String name){
		try {
			Statement stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT isvip FROM honey WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      int mo =0;
		      while(rs.next()){
		         mo = rs.getInt("isvip");
		      }
		      rs.close();
		      stmt.close();
		      if(mo == 1||mo == 3){
		    	  return true;
		      }else{
		    	  return false;
		      }
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


}
