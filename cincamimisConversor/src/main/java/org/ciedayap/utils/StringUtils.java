/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.utils;

import java.util.Optional;

/**
 * This class incorporates different methods for evaluating strings and/or characters
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class StringUtils {
    public final static String NULL_Pattern="<NULL>";    
    /**
     * It is responsible for knowing if the string as parameter is printable or not
     * @param c The string to be verified
     * @return TRUE if the string is printable, FALSE otherwise.
     */
    public static boolean isPrintable( String c ) 
    {
       if(c==null || c.trim().length()==0) return false;
        long novalidos=c.chars().mapToObj(i->(char)i)
                .filter(a-> !(Character.isISOControl(a) || Character.isLetterOrDigit(a)))
                .count();
        
       return (novalidos==0);       
    }

    /**
     * It is responsible for verifying if the string has characters which not be 
     * the white space
     * 
     * @param name the string to be verified
     * @return TRUE if the string contains characters (not only white spaces), FALSE otherwise.
     */
    public static boolean isEmpty(String name) {
        if(name==null || name.trim().length()==0) return true;
        
        return false;
    }
     
    /**
     * It verify if the string passed as parameter match with the NULL_pattern.
     * @param val The string to be compared
     * @return TRUE if the parameter is equal to the NULL_PATTERN, false otherwise.
     */
    public static boolean isNull(String val)
    {
        if(isEmpty(val)) return false;
        
        return NULL_Pattern.equalsIgnoreCase(val);
    }
    
    /**
     * It converts a string given as parameter in a Optional instance determining
     * the existence of value.
     * @param val The value to be evaluated
     * @return an Optional instance, indicating the value when be necessary.
     */
    public static Optional toNullableString(String val)
    {
        Optional ret;
        if(isEmpty(val) || isNull(val)) return Optional.empty();

        return Optional.of(val);
    }
}
