package fr.labri.starnet.policies.commons;

import fr.labri.starnet.Message;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 10/07/13
 * Time: 16:38
 * To change this template use File | Settings | File Templates.
 */
public interface MessageSet {
    /**
     * Add a message to the HelloSet structure if the sender of the message is not yet known.
     * Update the structure if the sender is already known.
     * @param m message to be added
     */
    public void add(Message m);

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
    public Message get(int index);

    /**
     *
     * @return the size of the data structure
     */
    public int size();

    /**
     * check if the message of the parameter m is already contains in the dataSet structure
     * @param m the message to be checked
     * @return true if the message of the parameter m is already contains in the structure, return false otherwise
     */
    public boolean contains(Message m);

    /**
     *
     * @return  the Collection of Message currently stored in the MessageSet structure
     */
    public Collection<Message> getAll();

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
