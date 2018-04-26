/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import org.ciedayap.cincamimis.Estimated;
import org.ciedayap.cincamimis.LikelihoodDistribution;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.Quantitative;

/**
 * This class incorporates a serie of utilities related to the Quantitative measure.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class QuantitativeUtils {
    /**
     * It answers about if the quantitivative value is deterministic.
     * 
     * @param value The quantitative value to be verified.
     * @return TRUE if the value is defined and it is deterministic. FALSE if the value
     * is defined but it is not deterministic, and NULL if the value is not defined.
     */
    public static Boolean isDeterministic(Quantitative value)
    {
        if(value==null) return null;
        
        BigDecimal bd=value.getDeterministicValue();
        
        return (bd!=null);
    }
    
    /**
     * This method indicates if the quantitative value is estimated or not.
     * @param value The quantitative instance to be verified.
     * @return TRUE if the quantitative value has an associated likelihood distribution with 2 or more estimated values.
     * FALSE if the quantitative value is deterministic or the likelihood distribution just have one estimated value. 
     * NULL when the likelihood distribution can not be verified even when it would be an estimated value.
     */
    public static Boolean isEstimated(Quantitative value)
    {
        if(value==null) return null;
        Boolean rdo=isDeterministic(value);
        if(rdo!=null && rdo) return Boolean.FALSE;
        
        LikelihoodDistribution ld=value.getLikelihoodDistribution();
        if(ld==null || ld.getLikelihoodDistributions()==null) return null;
        
        return (ld.getLikelihoodDistributions().size()>1);
    }
    
    /**
     * It is responsible for verifying if the quantitative value is deterministic
     * or estimated.
     * @param value The measure to be verified.
     * @return TRUE if the measure satisfies the requirements to be considered an
     * estimated or deterministic measure. FALSE otherwise.
     */
    public static Boolean isConsistent(Quantitative value)
    {
        Boolean rdo=isDeterministic(value);
        if(rdo!=null && rdo) return Boolean.TRUE;
        rdo=isEstimated(value);
        if(rdo!=null && rdo) return Boolean.TRUE;
        
        return Boolean.FALSE;
    }
    
    /**
     * It is responsible for verifying if the estimated value contains a 
     * valid pair <value,likelihhod>
     * @param e The estimated value to be verified
     * @return TRUE if the estimated value is complete and consistent,
     * FALSE otherwise
     */
    public static Boolean isComplete(Estimated e)
    {
        if(e==null || e.getLikelihood()==null || e.getValue()==null) return Boolean.FALSE;
        if(e.getLikelihood().compareTo(BigDecimal.ONE)>0) return Boolean.FALSE;//The likelihood can not be upper than 1
        if(e.getLikelihood().compareTo(BigDecimal.ZERO)<0) return Boolean.FALSE;//The likelihood can not be lower than 0
        if(e.getValue()==null) return false;
        
        return Boolean.TRUE;        
    }

    /**
     * It converts a quantitative value to a Optional instance. Even when the 
     * quantitative value be an estimated value, this method will use the mathematical expectation for
     * synthesizing to one single and representative value.
     * @param q The quantitative measure
     * @return a java.utils.Optional instance
     */
    public static Optional toNullableBigDecimal(Quantitative q) 
    {                
        if(q==null || !isConsistent(q)) return Optional.empty();
        
        Optional nbd;
        if(isDeterministic(q))
        {
            nbd=Optional.of(q.getDeterministicValue());
            return nbd;
        }
        
        BigDecimal me=null;
        try{
            nbd=Optional.of(mathematicalExpectation(q));
        }catch(LikelihoodDistributionException e)
        {
            nbd=Optional.empty();
        }

        return nbd;
    }
    
    /**
     * This method try to compute the mathematic expectation using the estimated 
     * values inside of the likelihood distribution.
     * @param value The estimated measure to be used for computing the mathematical expectation
     * @return The mathematical expectation. If the parameter is not an estimated measure, it will be returned null.
     * @throws LikelihoodDistributionException This exception is triggered if some 
     * estimated value inside of the lihelihood distribution is not complete (i.e. all the estimated
     * values should have the value and its associated likelihood)
     */
    public static BigDecimal mathematicalExpectation(Quantitative value) throws LikelihoodDistributionException
    {
        Boolean rdo=isEstimated(value);
        if(rdo!=null && !rdo) return null;
        
        LikelihoodDistribution ld=value.getLikelihoodDistribution();
        BigDecimal me=BigDecimal.ZERO;
        BigDecimal prob=BigDecimal.ZERO;
        for(Estimated val:ld.getLikelihoodDistributions())
        {
            if(!isComplete(val)) throw new LikelihoodDistributionException("There are at least one value in the Likelihood Distribution malformed");
            prob=prob.add(val.getLikelihood());//sum of the likelihood
            
            me=me.add(val.getLikelihood().multiply(val.getValue(), MathContext.DECIMAL128),MathContext.DECIMAL128);
        }
        
        return me;
    }
    
}
