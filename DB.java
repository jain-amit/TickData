/*
 * All files in this directory contain the intellectual property of Phient Inc.
 * These files can only be used as part of a Phient solution and on Phient equipment or Phient supplied equipment or Phient approved equipment.
 * No license to a third-party is expressed or implied by the files themselves or this license.
 * No warrants or guarantees are expressed and Phient excepts no liability for the use of this software under any circumstances.
 * These files cannot be moved, copied, modified, inspected, or used in any derivative work or other manner without the express written permission of Phient Inc.
 * All files © Copyright, Phient Inc. 2014
 */

package tickdata;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ajain
 */
public class DB
{
  private Connection conn;
  private PreparedStatement m_statementInsert = null;
  private PreparedStatement m_statementRetrieveAll = null;
  private PreparedStatement m_statementRetrieveDates = null;
  private PreparedStatement m_statementExists = null;
  private PreparedStatement m_statementDelete = null;
  private PreparedStatement m_statementDeleteDates = null;
  private PreparedStatement m_statementDeleteRecord = null;

  public DB() throws Exception
  {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    String home = System.getProperty("user.home");
    String db_directory = home + File.separator + "phientdb" + File.separator + "TickData" + File.separator + "db_file";
    conn = DriverManager.getConnection("jdbc:derby:" + db_directory +";create=true");

    DatabaseMetaData dmd = conn.getMetaData();
    String[] types = new String[1];
    types[0] = "TABLE";
    ResultSet rs = dmd.getTables("", "APP", "FIFTEEN_SECONDS", types);
    boolean exists = rs.next();
    rs.close();
    rs = null;
    dmd = null;

    if (exists == false)
    {
      update("CREATE TABLE fifteen_seconds (id int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),underlying VARCHAR(4) not null, tick_date CHAR(17) not null, tick_open double, tick_high double, tick_low double, tick_close double, tick_volume int)");
    }

    m_statementInsert = conn.prepareStatement("insert into fifteen_seconds (underlying, tick_date, tick_open, tick_high, tick_low, tick_close, tick_volume) values(?, ?, ?, ?, ?, ?, ?)");
    m_statementRetrieveAll = conn.prepareStatement("select * from fifteen_seconds where underlying = ? order by id");
    m_statementExists = conn.prepareStatement("select * from fifteen_seconds where underlying = ? and tick_date = ?");
    m_statementDelete = conn.prepareStatement("delete from fifteen_seconds where underlying = ? and tick_date = ?");
    m_statementRetrieveDates = conn.prepareStatement("select * from fifteen_seconds where underlying = ? and tick_date like ? order by id");
    m_statementDeleteRecord = conn.prepareStatement("delete from fifteen_seconds where id = ?");
    m_statementDeleteDates = conn.prepareStatement("delete from fifteen_seconds where underlying = ? and tick_date like ?");
  }

  private synchronized void update(String expression) throws SQLException
  {
    Statement st = null;
    st = conn.createStatement();
    int i = st.executeUpdate(expression);

    if (i == -1)
    {
      System.out.println("db error : " + expression);
    }

    st.close();
    st = null;
  }

  public synchronized boolean update(DataRow row)
  {
    boolean bInserted = false;
    try
    {
/*      m_statementExists.setString(1, row.getUnderlying());
      m_statementExists.setString(2, row.getDate());
      ResultSet rs = m_statementExists.executeQuery();
      m_statementExists.clearParameters();
      boolean success = rs.next();
      rs.close();
      rs = null;
      if (success == false)
      {
*/
        m_statementInsert.setString(1, row.getUnderlying());
        m_statementInsert.setString(2, row.getDate());
        m_statementInsert.setDouble(3, row.getOpen());
        m_statementInsert.setDouble(4, row.getHigh());
        m_statementInsert.setDouble(5, row.getLow());
        m_statementInsert.setDouble(6, row.getClose());
        m_statementInsert.setInt(7, row.getVolume());
        m_statementInsert.executeUpdate();
        m_statementInsert.clearParameters();
        bInserted = true;
//      }
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return bInserted;
  }

  public boolean exists(DataRow row)
  {
    boolean success = false;
    try
    {
      m_statementExists.clearParameters();
      m_statementExists.setString(1, row.getUnderlying());
      m_statementExists.setString(2, row.getDate());
      ResultSet rs = m_statementExists.executeQuery();
      success = rs.next();
      rs.close();
      rs = null;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return success;
  }

  public boolean delete(DataRow row)
  {
    boolean success = false;
    try
    {
      m_statementDelete.clearParameters();
      m_statementDelete.setString(1, row.getUnderlying());
      m_statementDelete.setString(2, row.getDate());
      int a = m_statementDelete.executeUpdate();
      success = true;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return success;
  }

  public boolean delete(int id)
  {
    boolean success = false;
    try
    {
      m_statementDeleteRecord.clearParameters();
      m_statementDeleteRecord.setInt(1, id);
      int a = m_statementDeleteRecord.executeUpdate();
      success = true;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return success;
  }

  public boolean deleteDates(String underlying, String date)
  {  
    boolean success = false;
    try
    {
      m_statementDeleteDates.clearParameters();
      m_statementDeleteDates.setString(1, underlying);
      m_statementDeleteDates.setString(2, date);
      int a = m_statementDeleteDates.executeUpdate();
      success = true;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return success;
  }

  public List <DataRow> getDataRows(String underlying, String date)
  {
    List <DataRow> drs = null;

    try
    {
      m_statementRetrieveDates.setString(1, underlying);
      m_statementRetrieveDates.setString(2, date);
      drs = getDataRows(m_statementRetrieveDates);
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return drs;
  }

  public List <DataRow> getDataRows(String underlying)
  {
    List <DataRow> drs = null;

    try
    {
      m_statementRetrieveAll.setString(1, underlying);
      drs = getDataRows(m_statementRetrieveAll);
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
    return drs;
  }

  private List <DataRow> getDataRows(PreparedStatement ps)
  {
    LinkedList <DataRow> drs = new LinkedList<DataRow>();
    try
    {
      ResultSet rs = ps.executeQuery();
      ps.clearParameters();
      ResultSetMetaData meta = rs.getMetaData();
      int colmax = meta.getColumnCount();
      Object o = null;

      for (; rs.next(); )
      {
        DataRow dr = new DataRow();
        for (int i = 0; i < colmax; ++i)
        {
          o = rs.getObject(i + 1);

          switch(i)
          {
            case 0:
              dr.setId(((Integer)o).intValue());
              break;
            case 1:
              dr.setUnderlying((o.toString()));
              break;
            case 2:
              dr.setDate(o.toString());
              break;
            case 3:
              dr.setOpen(((Double)o).doubleValue());
              break;
            case 4:
              dr.setHigh(((Double)o).doubleValue());
              break;
            case 5:
              dr.setLow(((Double)o).doubleValue());
              break;
            case 6:
              dr.setClose(((Double)o).doubleValue());
              break;
            case 7:
              dr.setVolume(((Integer)o).intValue());
              break;
            default:
              break;
          }
        }
        drs.add(dr);
      }
      rs.close();
      rs = null;
    }
    catch (SQLException sqle)
    {
      sqle.printStackTrace();
    }
      return drs;
  }
}