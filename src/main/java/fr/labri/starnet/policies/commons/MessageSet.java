package fr.labri.starnet.policies.commons;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 10/07/13
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 * @param <E>
 */
public interface MessageSet<E> extends Collection<E>{

    /**
     * Clean the MessageSet structure by removing all Entry that are older than the timeout parameter
     * @param timeout the time a Message is considered alive
     * @param currentTime the current time of the system
     */
    public void clean(long timeout, long currentTime);

    /**
     * return a message corresponding to the index given in param index
     * @param index  the index of the message to be retrieved
     * @return the Message retrieved
     */
    public E get(int index);

    /**
     *
     * @return the size of the data structure
     */
    public int size();

    /**
     *
     * @return  the Collection of Message currently stored in the MessageSet structure
     */
    public Collection<E> getAll();

    /**
     * clear the MessageSet structure
     */
    public void clear();

    /**
     *
     * @return  true if the MessageSet structure is empty, false otherwise
     */
    public boolean isEmpty();

}
