package me.alf21.housesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;

/**
 * Created by Marvin on 26.05.2014.
 * Edited & Added Funktions by Alf21.
 **/
public class MysqlConnection {
    private boolean initialized;
    private Connection connection;
    public boolean initConnection() {
        if(!initialized) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                File fl = new File(HouseSystem.getInstance().getDataDir(), "mysql.txt");
                if (fl.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(fl));
                    String line;
                    while (reader.ready()) {
                        line = reader.readLine();
                        String[] parts = line.split("[,]");
                        if (line.length() > 4) {
                            if (parts.length == 4)
                                connection = DriverManager.getConnection("jdbc:mysql://" + parts[0] + "/" + parts[1], parts[2], parts[3]);
                            else if (parts.length == 3)
                                connection = DriverManager.getConnection("jdbc:mysql://" + parts[0] + "/" + parts[1], parts[2], null);
                            initialized = true;
                            break;
                        }
                    }
                    reader.close();
                } else {
                    fl.createNewFile();
                    HouseSystem.getInstance().getLoggerInstance().info("[Fehler] Die Mysql Datei, wurde so eben erst erstellt!");
                    HouseSystem.getInstance().getShoebill().getSampObjectManager().getServer().sendRconCommand("exit");
                    return false;
                }
            } catch (Exception ex) {
                HouseSystem.getInstance().getLoggerInstance().info("[Fehler] Verbindung zum MysqlServer konnte nicht hergestellt werden!");
                HouseSystem.getInstance().getShoebill().getSampObjectManager().getServer().sendRconCommand("exit");
                return false;
            }
        }
        return true;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void makeDatabase() {
        try {
            Statement stmnt = connection.createStatement();
            if (connection != null && connection.isValid(1000)) {
            	stmnt.executeUpdate("CREATE TABLE IF NOT EXISTS samp_housesystem (Id INTEGER PRIMARY KEY AUTO_INCREMENT, player CHAR(24), level INTEGER NOT NULL DEFAULT '1', " +
                			"house_model INTEGER NOT NULL DEFAULT '0', " +
                			"house_level INTEGER NOT NULL DEFAULT '1', " +
                			"house_price INTEGER NOT NULL DEFAULT '0', " +
                			"house_spawn BOOL NOT NULL DEFAULT '1', " +
                			"house_X FLOAT, " + 
                			"house_Y FLOAT, " + 
                			"house_Z FLOAT, " +
                			"spawn_X FLOAT NOT NULL DEFAULT '0', " +
                			"spawn_Y FLOAT NOT NULL DEFAULT '0', " +
                			"spawn_Z FLOAT NOT NULL DEFAULT '0')");
    		} else {
                HouseSystem.getInstance().getLoggerInstance().info("Mysql Datenbank konnte nicht erstellt werden.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	public int executeUpdate(String query) {
        try {
            if (connection != null && connection.isValid(1000)) {
                Statement stmnt = connection.createStatement();
                stmnt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                try {
                    ResultSet rs = stmnt.getGeneratedKeys();
                    rs.next();
                    return rs.getInt(1);
                } catch (Exception ignored) { }
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
        Statement statement;
        try {
            if (connection != null && connection.isValid(1000)) {
                statement = connection.createStatement();
                rs = statement.executeQuery(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

	public boolean check(String table, String field, String str) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM %s WHERE %s = '%s'", table, field, str);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					try {
						if(rs.getString(field).toLowerCase().equals(str.toLowerCase())){
							return true;
						}
					} catch (Exception e){
						return false;
					}
				}
            }
			else {
				return false;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return false;   
	}

	public float getFloat(String table, String playerName, String field) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM %s WHERE player = '%s'", table, playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					try {
						return rs.getFloat(field);
					} catch (Exception e){
						return 0.0f;
					}
				}
            }
			else {
				return 0.0f;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return 0.0f;   
	}

	public String getHouseOwner(int houseId) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE Id = '%d'", houseId);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return rs.getString("player");
				}
            }
			else {
				return null;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return null;
	}

	public Integer getHouseId(String playerName) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return rs.getInt("Id");
				}
            }
			else {
				return 0;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return 0;   
	}
	
	public Location getHouseLocation(String playerName) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return new Location(rs.getFloat("house_X"), rs.getFloat("house_Y"), rs.getFloat("house_Z"));
				}
            }
			else {
				return null;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return null;   
	}
	
	public Integer getHouseModel(String playerName) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return rs.getInt("house_model");
				}
            }
			else {
				return 0;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return 0;   
	}
	
	public Integer getHouseLevel(String playerName) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return rs.getInt("house_level");
				}
            }
			else {
				return 0;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return 0;   
	}
	
	public boolean isHouseSpawn(String playerName) {
        ResultSet rs;
        Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return rs.getBoolean("house_spawn");
				}
            }
			else {
				return false;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return false;   
	}

	public boolean exist(String playerName) {
		ResultSet rs = null;
		Statement statement;
        String query = String.format("SELECT * FROM samp_housesystem WHERE player = '%s'", playerName);
        try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                rs = statement.executeQuery(query);
				if(rs.first()){
					return true;
				}
            }
			else {
				return false;
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
		return false;
	}

	public void createHouse(String playerName, int modelId, float x, float y, float z) {
		try {
			if (connection != null && connection.isValid(1000)) {
				executeUpdate("INSERT INTO samp_housesystem (player, house_model, house_X, house_Y, house_Z) VALUES ('"+playerName+"', '"+modelId+"', '"+x+"', '"+y+"', '"+z+"')");
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
		}
	}

	public void deleteHouse(String playerName) {
		try {
			if (connection != null && connection.isValid(1000)) {
				executeUpdate(String.format("DELETE FROM samp_housesystem WHERE player = '%s'", playerName));
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
		}
	}

	public void deleteHouse(int houseId) {
		try {
			if (connection != null && connection.isValid(1000)) {
				executeUpdate(String.format("DELETE FROM samp_housesystem WHERE Id = '%s'", houseId));
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
		}
	}

	public void updateHouse(String playerName, String str, int value) {
		Statement statement;
		try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                statement.execute(String.format("UPDATE samp_housesystem SET %s = '%d' WHERE player = '%s'",
						str.toLowerCase(), value, playerName));
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
	}

	public void updateHouseLocation(String playerName, Location location) {
		Statement statement;
		try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                statement.execute("UPDATE samp_housesystem SET house_X = '"+location.x+"', house_Y = '"+location.y+"', house_Z = '"+location.z+"' WHERE player = '"+playerName+"'");
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
	}

	public void updateSpawnLocation(String playerName, Vector3D vector3d) {
		Statement statement;
		try {
            if (connection != null && connection.isValid(1000)) {
            	statement = connection.createStatement();
                statement.execute("UPDATE samp_housesystem SET spawn_X = '"+vector3d.x+"', spawn_Y = '"+vector3d.y+"', spawn_Z = '"+vector3d.z+"' WHERE player = '"+playerName+"'");
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
        }
	}

//
	public String aCreateHouse(String part, int modelId, float x, float y, float z) {
		try {
			int Id = 0;
			if (connection != null && connection.isValid(1000)) {
				Id = executeUpdate("INSERT INTO samp_housesystem (house_model, house_X, house_Y, house_Z) VALUES ('"+modelId+"', '"+x+"', '"+y+"', '"+z+"')");
				if (Id != 0) {
					part += Id;
					executeUpdate("UPDATE samp_housesystem SET player = '"+part+"' WHERE Id = '"+Id+"'");
					return part;
				}
			}
		} catch (SQLException e) {
            System.out.print("ERROR - Stacktrace : ");
            e.printStackTrace();
		}
		return null;
	}
}