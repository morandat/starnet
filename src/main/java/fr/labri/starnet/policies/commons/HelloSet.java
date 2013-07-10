package fr.labri.starnet.policies.commons;

import fr.labri.starnet.Address;
import fr.labri.starnet.Message;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 10/07/13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class HelloSet implements MessageSet {

    Map<Address, Message> helloMap;
    List<Address> addressList;

    public HelloSet(){
        helloMap = new HashMap<Address,Message>();
        addressList = new ArrayList<Address>();
    }

    @Override
    public void add(Message m){
        if(helloMap.put(m.getSenderAddress(), m) == null){
            addressList.add(m.getSenderAddress());
        }
    }

    @Override
    public void clean(long timeout, long currentTime){
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

    @Override
    public Message get(int index){
        return helloMap.get(addressList.get(index));
    }

    @Override
    public int size(){
        return addressList.size();
    }

    @Override
    public boolean contains(Message m){
        return helloMap.containsKey(m.getSenderAddress());
    }

    @Override
    public Collection<Message> getAll() {
        return helloMap.values();
    }

    @Override
    public void clear() {
        helloMap.clear();
        addressList.clear();
    }

    @Override
    public boolean isEmpty() {
        return addressList.isEmpty();
    }

}
