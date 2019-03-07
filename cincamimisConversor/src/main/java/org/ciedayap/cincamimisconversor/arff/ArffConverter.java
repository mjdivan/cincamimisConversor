/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.cincamimisconversor.arff;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.cincamimisconversor.Attribute;
import org.ciedayap.cincamimisconversor.CincamimisWindow;
import org.ciedayap.cincamimisconversor.Tuple;
import org.ciedayap.utils.QuantitativeUtils;
import org.ciedayap.utils.StringUtils;

/**
 * It is responsible for translating to ARFF data format from a given source
 * 
 * @author Mario Diván
 * @version 1.0 
 */
public class ArffConverter {
    /**
     * It makes the conversion from the data type related to the attributes in relation to the Weka's data types.
     * 
     * @param tmp The Attribute metadata
     * @return The corresponding weka data type. The date, time and zone time are managed such as integer and string in a break-down way.
     */
    public static String typeCast(Attribute tmp)
    {
        if(tmp==null) return null;
        if(tmp.isQuantitative()) return "NUMERIC";
        
        return "STRING";
    }
        
    /**
     * It takes a columnar-data window and translates it to a String organized under the ARFF data format.
     * The number of tuples to be included in the output could be limited through the threshold parameter with a positive integer.
     * @param win The columnar-data window
     * @param relationName The relation name to be assigned in the ARFF data format
     * @param threshold It represents the max limits of tuples to be included in the ARFF content, independently that the number of tuples in the window is greater.
     * When the threshold parameter is lesser or equal to zero (0), all the content will be included in the result.
     * @return An String with the Window's content organized under the ARFF data format. It could be limited depending the value of threshold.
     * @throws LikelihoodDistributionException The likelihood distributions are converted to a unique value through the mathematical expectation. 
     * If any inconvenience is found during the calculating, this exception will be raised.
     */
    public static String fromWindow(CincamimisWindow win,String relationName,long threshold) throws LikelihoodDistributionException            
    {
        if(win==null) return null;
        if(win.getState()!=CincamimisWindow.CREATED) return null;
        if(win.getRowCount()==0) return null;
        
        StringBuilder sb=new StringBuilder();
        
        ZonedDateTime zdt=ZonedDateTime.now();
        
        sb.append("%").append("1. Title: ").append(relationName).append("\n");
        sb.append("%").append("2. Source: ").append("\n");
        sb.append("%").append("\ta. Creator: CincamimisConversor Library").append("\n");
        sb.append("%").append("\tb. Donor: ").append("\n");
        sb.append("%").append("\tc. Date: ").append(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zdt)).append("\n");
        sb.append("%").append("\n");
        
        if(StringUtils.isEmpty(relationName))
        {
            sb.append("@RELATION").append(" CincamimisConversor").append("\n");
        }
        else
        {            
            sb.append("@RELATION ").append(relationName).append("\n");
        }
        
        sb.append("\n");
        
        
        DecimalFormatSymbols decSymbol = new DecimalFormatSymbols();
        decSymbol.setDecimalSeparator('.');
        DecimalFormat df=new DecimalFormat("#######0.000");
        df.setDecimalFormatSymbols(decSymbol);

        //It defines the attributes
        for(int i=0;i<win.getRowCount() && (threshold<=0 || i<threshold);i++)
        {
          if(i==0)
          {//It is just performed for the first row            
            for(int j=0;j<win.columnCount();j++)
            {
                String name=win.getColumnMetadata(j).getName();
                sb.append("@ATTRIBUTE ").append(name).append(" ")
                        .append(typeCast(win.getColumnMetadata(j))).append("\n");                
            }            
            
            sb.append("\n");
            sb.append("@DATA").append("\n");            
          }
               
            Tuple t=win.getRowAt(i);
            
            for(int j=0;j<win.columnCount();j++)
              {
                  String xVal;
                  
                  if(win.getColumnMetadata(j).isQuantitative())
                  {
                    Quantitative value=(Quantitative)t.getValue(win.getColumnMetadata(j));

                    if(value!=null && QuantitativeUtils.isConsistent(value))
                    {
                        if(QuantitativeUtils.isDeterministic(value))
                        {
                            xVal=df.format(value.getDeterministicValue());
                        }
                        else
                        {
                            xVal=df.format(QuantitativeUtils.mathematicalExpectation(value));
                        }
                    }else xVal="?";//? It indicates a null value
                  }
                  else
                  {
                    String value=(String)t.getValue(win.getColumnMetadata(j));

                    if(!StringUtils.isNull(value))
                    {
                        xVal=value;
                    }else xVal="?";//? It indicates a null value
                  }
                  
                  if(xVal!=null && xVal.equalsIgnoreCase("NULL"))
                  {
                      
                  }
                  else 
                  {
                      if(j==0)
                      {
                          sb.append(xVal);                          
                          if(win.columnCount()>1)
                          {
                              sb.append(",");
                          }
                      }
                      else
                      {
                          sb.append(xVal);
                          if((j+1)<win.columnCount())
                          {
                              sb.append(",");
                          }                                 
                      }
                  }
                  
              }
            
              //End of data line
              sb.append("\n");
            }
        
        return sb.toString();
    }     

    /**
     * It is responsible to carry forward the converion from the columnar-data organization in the window to a set of
     * ARFF contents partitioned by a given size.
     * 
     * @param win The columnar-data window to be conversed
     * @param relationName The relation name to be given in the ARFF data format
     * @param partitionSize The maximum number of tuples to be held in each partition. 
     * The first and intermediate partitions will have a number of ´partitionsize´ tuples, while the last partition
     * could have a number of tuples up to "partitionsize" or lesser tuples (i.e. depending the remaining number of tuples)
     * @return An ArrayList in which each position corresponds with a partition organized in terms of the ARFF data format. Each partition
     * could be saved in a file like an independent ARFF file.
     * @throws LikelihoodDistributionException The likelihood distributions are converted to a unique value through the mathematical expectation. 
     * If any inconvenience is found during the calculating, this exception will be raised.
     */
    public static ArrayList<String> partitionFromWindow(CincamimisWindow win,String relationName,long partitionSize) throws LikelihoodDistributionException            
    {
        if(win==null) return null;
        if(win.getState()!=CincamimisWindow.CREATED) return null;
        if(win.getRowCount()==0) return null;
        if(partitionSize<=0) return null;
        
        StringBuilder metadata=new StringBuilder();
        StringBuilder data=new StringBuilder();
        
        ZonedDateTime zdt=ZonedDateTime.now();
        
        metadata.append("%").append("1. Title: ").append(relationName).append("\n");
        metadata.append("%").append("2. Source: ").append("\n");
        metadata.append("%").append("\ta. Creator: CincamimisConversor Library").append("\n");
        metadata.append("%").append("\tb. Donor: ").append("\n");
        metadata.append("%").append("\tc. Date: ").append(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zdt)).append("\n");
        metadata.append("%").append("\n");
        
        if(StringUtils.isEmpty(relationName))
        {
            metadata.append("@RELATION").append(" CincamimisConversor").append("\n");
        }
        else
        {            
            metadata.append("@RELATION ").append(relationName).append("\n");
        }
        
        metadata.append("\n");
        ArrayList<String> results=new ArrayList<>();
        
        DecimalFormatSymbols decSymbol = new DecimalFormatSymbols();
        decSymbol.setDecimalSeparator('.');
        DecimalFormat df=new DecimalFormat("#######0.000");
        df.setDecimalFormatSymbols(decSymbol);

        long currentSlot=0;
        //It defines the attributes
        for(int i=0;i<win.getRowCount();i++)
        {
          if(i==0)
          {//It is just performed for the first row            
            for(int j=0;j<win.columnCount();j++)
            {
                String name=win.getColumnMetadata(j).getName();
                metadata.append("@ATTRIBUTE ").append(name).append(" ")
                        .append(typeCast(win.getColumnMetadata(j))).append("\n");                
            }            
            
            metadata.append("\n");
            metadata.append("@DATA").append("\n");            
          }
               
            long slot= (i/partitionSize);
            if(currentSlot!=slot)
            {
                StringBuilder c=new StringBuilder();
                c.append(metadata.toString()).append("\n");
                c.append(data.toString()).append("\n");
                
                results.add(c.toString());
                data.delete(0, data.length());
                currentSlot=slot;
            }
            
            Tuple t=win.getRowAt(i);
            
            for(int j=0;j<win.columnCount();j++)
              {
                  String xVal;
                  
                  if(win.getColumnMetadata(j).isQuantitative())
                  {
                    Quantitative value=(Quantitative)t.getValue(win.getColumnMetadata(j));

                    if(value!=null && QuantitativeUtils.isConsistent(value))
                    {
                        if(QuantitativeUtils.isDeterministic(value))
                        {
                            xVal=df.format(value.getDeterministicValue());
                        }
                        else
                        {
                            xVal=df.format(QuantitativeUtils.mathematicalExpectation(value));
                        }
                    }else xVal="?";//? It indicates a null value
                  }
                  else
                  {
                    String value=(String)t.getValue(win.getColumnMetadata(j));

                    if(!StringUtils.isNull(value))
                    {
                        xVal=value;
                    }else xVal="?";//? It indicates a null value
                  }
                  
                  if(xVal!=null && xVal.equalsIgnoreCase("NULL"))
                  {
                      
                  }
                  else 
                  {
                      if(j==0)
                      {
                          data.append(xVal);                          
                          if(win.columnCount()>1)
                          {
                              data.append(",");
                          }
                      }
                      else
                      {
                          data.append(xVal);
                          if((j+1)<win.columnCount())
                          {
                              data.append(",");
                          }                                 
                      }
                  }
                  
              }
            
              //End of data line
              data.append("\n");
            }
        
        //The last loop cycle
        if(data.length()>1)
        {
            StringBuilder c=new StringBuilder();
            c.append(metadata.toString()).append("\n");
            c.append(data.toString()).append("\n");

            results.add(c.toString());
            data.delete(0, data.length());            
        }

        return results;
    }     
    
    /**
     * It converts the columnar-data organization from the window to an ARFF data file.
     * @param win The columnar-data window to be converted
     * @param relationName The relation name to be given in the ARFF metadata
     * @param fos The FileOutputStream in an open state and ready to write any content
     * @param encoding The encoding to be used in the writing
     * @return TRUE when the ARFF content could be translated and written in the ARFF file.
     * @throws LikelihoodDistributionException The likelihood distributions are converted to a unique value through the mathematical expectation. 
     * If any inconvenience is found during the calculating, this exception will be raised.
     * @throws UnsupportedEncodingException It is raised when the indicated encoding is not supported by the system
     * @throws IOException It is raised when some problem happens at the moment in which the content needs to be written.
     */
    public static boolean fromWindowToFile(CincamimisWindow win,String relationName,FileOutputStream fos, String encoding) throws LikelihoodDistributionException, UnsupportedEncodingException, IOException                    
    {
        String content=fromWindow(win,relationName,-1);
        if(StringUtils.isEmpty(content) || fos==null) return false;
        
        fos.write(content.getBytes(Charset.forName(encoding)));
        fos.close();
        
        return true;        
    }

    /**
     * It converts the columnar-data organization from the window to an ARFF data file.
     * @param win The columnar-data window to be converted
     * @param relationName The relation name to be given in the ARFF metadata
     * @param fos The FileOutputStream in an open state and ready to write any content
     * @param encoding The encoding to be used in the writing
     * @param threshold It limits the number of tuples to be incorporated in the ARFF data file
     * @return TRUE when the ARFF content could be translated and written in the ARFF file.
     * @throws LikelihoodDistributionException The likelihood distributions are converted to a unique value through the mathematical expectation. 
     * If any inconvenience is found during the calculating, this exception will be raised.
     * @throws UnsupportedEncodingException It is raised when the indicated encoding is not supported by the system
     * @throws IOException It is raised when some problem happens at the moment in which the content needs to be written.
     */    
    public static boolean fromWindowToFile(CincamimisWindow win,String relationName,FileOutputStream fos, String encoding,long threshold) throws LikelihoodDistributionException, UnsupportedEncodingException, IOException                
    {
        String content=fromWindow(win,relationName,threshold);
        if(StringUtils.isEmpty(content) || fos==null) return false;
        
        fos.write(content.getBytes(Charset.forName(encoding)));
        fos.close();
        
        return true;        
    }
}
