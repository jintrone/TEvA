package edu.mit.cci.util;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 5/23/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Filter<T> {

    public boolean accept(T obj);
}
