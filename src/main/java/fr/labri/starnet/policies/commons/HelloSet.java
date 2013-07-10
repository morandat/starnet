package fr.labri.starnet.policies.commons;

import fr.labri.starnet.Address;
import fr.labri.starnet.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 10/07/13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class HelloSet {

    Map<Address, Message> helloMap;
    List<Address> addressList;

    public HelloSet(){
        helloMap = new HashMap<Address,Message>();
        addressList = new ArrayList<Address>();
    }

    /**
     * Add a message to the HelloSet structure if the sender of the message is not yet known.
     * Update the structure if the sender is already known.
     * @param m message to be added
     */
    public void add(Message m){
        if(helloMap.put(m.getSenderAddress(), m) == null){
            addressList.add(m.getSenderAddress());
        }
    }

    /**
     * Clean the HelloSet structure by removing all Hello Entry that are older than the timeout parameter
     * @param timeout the time a HelloMessage is considered alive
     * @param currentTime the current time of the system
     */
    public void clean (long timeout, long currentTime){
        List<Address> toRemove = new ArrayList<Address>();
        for(Address ad : helloMap.keySet()){
            if ((currentTime - helloMap.get(ad).getEmitTime() ) > timeout){
                toRemove.add(ad);
            }
        }
        for(Address ad : toRemove){
            helloMap.remove(ad);
            addressList.remove(ad);
        }
    }

    /**
     * return a message corresponding to the index given in param index
     * @param index  the index of the message to be retrieved
     * @return the Message retrieved
     */
    public Message get(int index){
        return helloMap.get(addressList.get(index));
    }

    public int size(){
        return addressList.size();
    }

}
