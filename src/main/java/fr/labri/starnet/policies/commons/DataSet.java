package fr.labri.starnet.policies.commons;

import java.util.*;

import fr.labri.starnet.Message;

/**
 * Created with IntelliJ IDEA.
 * User: jbourcie
 * Date: 10/07/13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class DataSet implements MessageSet{

    Map<Long, Message> dataMap;
    List<Long> dataList;

    public DataSet(){
        dataMap = new HashMap<Long,Message>();
        dataList = new ArrayList<Long>();
    }

    @Override
    public void add(Message m){
        if(dataMap.put(m.getMessageID(), m) == null){
            dataList.add(m.getMessageID());
        }
    }

    @Override
    public void clean (long timeout, long currentTime){
        List<Long> toRemove = new ArrayList<Long>();
        for(long id : dataMap.keySet()){
            if ((currentTime - dataMap.get(id).getEmitTime() ) > timeout){
                toRemove.add(id);
            }
        }
        for(Long id : toRemove){
            dataMap.remove(id);
            dataList.remove(id);
        }
    }

    @Override
    public Message get(int index){
        return dataMap.get(dataList.get(index));
    }

    @Override
    public boolean contains(Message m){
         return dataMap.containsKey(m.getMessageID());
    }

    @Override
    public int size(){
        return dataList.size();

    }

    public Collection<Message> getAll(){
        return dataMap.values();
    }

    @Override
    public void clear() {
        dataMap.clear();
        dataList.clear();
    }

    @Override
    public boolean isEmpty() {
        return dataList.isEmpty();
    }
}
