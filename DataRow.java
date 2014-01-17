/*
 * All files in this directory contain the intellectual property of Phient Inc.
 * These files can only be used as part of a Phient solution and on Phient equipment or Phient supplied equipment or Phient approved equipment.
 * No license to a third-party is expressed or implied by the files themselves or this license.
 * No warrants or guarantees are expressed and Phient excepts no liability for the use of this software under any circumstances.
 * These files cannot be moved, copied, modified, inspected, or used in any derivative work or other manner without the express written permission of Phient Inc.
 * All files © Copyright, Phient Inc. 2014
 */

package tickdata;

/**
 *
 * @author ajain
 */
public class DataRow
{
  private String m_underlying;
  private String m_date;
  private double m_open = -1;
  private double m_high = -1;
  private double m_low = -1;
  private double m_close = -1;
  private int m_volume = 0;
  private int m_id = -1;

  public DataRow()
  {
  }

  public void setId(int id)
  {
    m_id = id;
  }

  public int getId()
  {
    return m_id;
  }

  public void setUnderlying(String underlying)
  {
    m_underlying = underlying;
  }

  public String getUnderlying()
  {
    return m_underlying;
  }

  public void setDate(String date)
  {
    m_date = date;
  }

  public String getDate()
  {
    return m_date;
  }

  public void setOpen(double open)
  {
    m_open = open;
  }

  public double getOpen()
  {
    return m_open;
  }

  public void setHigh(double high)
  {
    m_high = high;
  }

  public double getHigh()
  {
    return m_high;
  }

  public void setLow(double low)
  {
    m_low = low;
  }

  public double getLow()
  {
    return m_low;
  }

  public void setClose(double close)
  {
    m_close = close;
  }

  public double getClose()
  {
    return m_close;
  }

  public void setVolume(int volume)
  {
    m_volume = volume;
  }

  public int getVolume()
  {
    return m_volume;
  }
}