/*
 * All files in this directory contain the intellectual property of Phient Inc.
 * These files can only be used as part of a Phient solution and on Phient equipment or Phient supplied equipment or Phient approved equipment.
 * No license to a third-party is expressed or implied by the files themselves or this license.
 * No warrants or guarantees are expressed and Phient excepts no liability for the use of this software under any circumstances.
 * These files cannot be moved, copied, modified, inspected, or used in any derivative work or other manner without the express written permission of Phient Inc.
 * All files © Copyright, Phient Inc. 2014
 */

package tickdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ajain
 */
public class Main
{
  public final static SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
  public final static SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
  public final static SimpleDateFormat PROCESS_DATE_TIME = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
  private final static SimpleDateFormat OFFLINE_DATE_TIME_DOUBLE_DIGIT_HOUR = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args)
  {
    // TODO code application logic here
    Calendar cal = Calendar.getInstance();
    int month = cal.get(Calendar.MONTH) + 1;
    String m = "";
    String y = Integer.toString(cal.get(Calendar.YEAR));

    if (month < 10)
    {
      m = "0";
    }

    y = y + m + Integer.toString(month) + "%";

    process("CL", y);
    process("TF", y);
    process("EMD", y);
    process("NQ", y);
    process("YM", y);
    process("ES", y);
    process("GC", y);
    process("NG", y);

/*
    try
    {
      DB db = new DB();
      db.deleteDates("", "");
    }
    catch (Exception e)
    {
    }
*/
 }

  private static void process(String underlying, String pd)
  {
    try
    {
      DB db = new DB();

      BufferedReader reader = null;

      String db_directory = ".." + File.separator + "Purchase" + File.separator + underlying + "_History.txt";

      File fileExists = new File(db_directory);

      String line = null;
      DataRow dr = new DataRow();
      String[] params;

      if (fileExists.exists())
      {
        reader = new BufferedReader(new FileReader(db_directory));
        line = reader.readLine();
        line = reader.readLine();
      }
      else
      {
        System.out.println("No data for " + underlying);
      }

      if (line != null)
      {
        params = line.split(",");
        dr.setUnderlying(params[0]);
        dr.setDate(params[1]);
        dr.setOpen(Double.parseDouble(params[2]));
        dr.setHigh(Double.parseDouble(params[3]));
        dr.setLow(Double.parseDouble(params[4]));
        dr.setClose(Double.parseDouble(params[5]));
        dr.setVolume(Integer.parseInt(params[6]));

        params = dr.getDate().split(" ");

        if (!db.exists(dr) && params[1].equalsIgnoreCase("06:30:00"))
        {
          db.update(dr);

          while ((line = reader.readLine()) != null)
          {
            params = line.split(",");
            dr.setUnderlying(params[0]);
            dr.setDate(params[1]);
            dr.setOpen(Double.parseDouble(params[2]));
            dr.setHigh(Double.parseDouble(params[3]));
            dr.setLow(Double.parseDouble(params[4]));
            dr.setClose(Double.parseDouble(params[5]));
            dr.setVolume(Integer.parseInt(params[6]));
            db.update(dr);
          }
        }
        else
        {
          System.out.println("Already Exists for " + underlying);
        }
        reader.close();
        fileExists.delete();
      }

      FileWriter file = null;
      FileWriter file2 = null;

      try
      {
        file = new FileWriter(underlying + ".txt", false);
        file.write("name,date,open,high,low,close,volume\r\n");
      }
      catch (IOException e)
      {
        file = null;
      }

      List<DataRow> drs;
      if (pd == null || pd.equalsIgnoreCase(""))
      {
        drs = drs = db.getDataRows(underlying);
      }
      else
      {
        drs = drs = db.getDataRows(underlying, pd);
      }

      Date tick_date = null;
      double open = 0;
      double high = 0;
      double low = 0;
      double close = 0;
      String cur = null;
      int volume = 0;
      Date everyDate = null;
      for (DataRow d : drs)
      {
        try
        {
          everyDate = DATE_TIME.parse(d.getDate());
        }
        catch (ParseException pe)
        {
          pe.printStackTrace();
        }

        if (d.getDate().endsWith("00"))
        {
          open = d.getOpen();
          high = d.getHigh();
          low = d.getLow();            

          try
          {
            tick_date = DATE_TIME.parse(d.getDate());
          }
          catch (ParseException pe)
          {
            pe.printStackTrace();
          }

          if (cur == null || !cur.equalsIgnoreCase(d.getDate().substring(4, 6)))
          {
            cur = d.getDate().substring(4, 6);

            if (file != null)
            {
              file.close();
            }
            file = new FileWriter(underlying + ".txt", false);
            file.write("name,date,open,high,low,close,volume\r\n");

            if (file2 != null)
            {
              file2.close();
            }
            file2 = new FileWriter(underlying + "_15_out.csv", false);
          }
        }

        if (high < d.getHigh())
        {
          high = d.getHigh();
        }

        if (low > d.getLow())
        {
          low = d.getLow();
        }

        volume += d.getVolume();

        if (d.getDate().endsWith("45"))
        {
          close = d.getClose();
          file.write(underlying);
          file.write(",");
          file.write(OFFLINE_DATE_TIME_DOUBLE_DIGIT_HOUR.format(tick_date));
          file.write("," + open);
          file.write("," + high);
          file.write("," + low);
          file.write("," + close);
          file.write("," + volume);
          file.write("\r\n");
          volume = 0;
        }

        file2.write(PROCESS_DATE_TIME.format(everyDate));
        file2.write(",,,,");
        file2.write(underlying);
        file2.write(",");
        file2.write(d.getDate());
        file2.write("," + d.getOpen());
        file2.write("," + d.getHigh());
        file2.write("," + d.getLow());
        file2.write("," + d.getClose());
        file2.write("," + d.getVolume());
        file2.write("\r\n");
      }

      file.close();
      file = null;
      file2.close();;
      file2 = null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


  private static void process(String underlying)
  {
    try
    {
      DB db = new DB();

      FileWriter file = null;

      try
      {
        file = new FileWriter(underlying + "_15_out.csv", false);
      }
      catch (IOException e)
      {
        file = null;
      }

      List<DataRow> drs;
      drs = drs = db.getDataRows(underlying);


      Date everyDate = null;
      for (DataRow d : drs)
      {
        try
        {
          everyDate = DATE_TIME.parse(d.getDate());
        }
        catch (ParseException pe)
        {
          pe.printStackTrace();
        }

        file.write(PROCESS_DATE_TIME.format(everyDate));
        file.write("," + underlying);
        file.write("," + d.getOpen());
        file.write("," + d.getHigh());
        file.write("," + d.getLow());
        file.write("," + d.getClose());
        file.write("," + d.getVolume());
        file.write("\r\n");
      }
      file.close();;
      file = null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
