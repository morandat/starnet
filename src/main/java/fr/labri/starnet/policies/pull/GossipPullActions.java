package fr.labri.starnet.policies.pull;


import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

import java.util.*;


public class GossipPullActions {

    public static final String HELLO_SET = "hello_set";
    public static final String SAVED_MAILBOX = "saved_mailbox";
    public static final String CURRENT_MESSAGE = "current";
    public static final String DATA_SET ="data_set";
    public static final String OLD_DATA_SET ="old_data_set";

    public static final String ID ="data_set";
    public static final String POS ="data_set";


    public static class SendHello extends StateAdapter<INode> {
        @Override
        public void preAction(INode context, ITimedAutomata<INode> auto) {
            context.send(context.createMessage(Message.Type.HELLO));

        }
    }

    public static class SendAndFlushMsgDataSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(GossipPullActions.CURRENT_MESSAGE);

            double distance = context.getPosition().getNorm(currentMessage.getSenderPosition());
            double transmissionPower = distance/context.getDescriptor().getEmissionRange();

            HashSet<Message> datas = (HashSet<Message>)storage.get(GossipPullActions.DATA_SET);
            for (Message message : datas) {
                context.send(transmissionPower, message);
            }
            datas.clear();
        }
    }

    public static class SaveMailbox extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            List<Message> list = Arrays.asList(context.receive());
            Stack<Message> stack = new Stack<Message>();
            stack.addAll(list);
            storage.put(GossipPullActions.SAVED_MAILBOX, stack);
        }
    }

    public static class AddToMsgDataSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(GossipPullActions.CURRENT_MESSAGE);

            //check if this the first message
            if (!storage.containsKey(GossipPullActions.OLD_DATA_SET)){
                storage.put(GossipPullActions.OLD_DATA_SET, new HashSet<Message>());
            }
            // check if already received message
            HashSet<Message> oldDatas = (HashSet<Message>)storage.get(GossipPullActions.OLD_DATA_SET);
            if (!oldDatas.contains(currentMessage)){
                //if not already received add to the current data set
                HashSet<Message> hs = (HashSet<Message>)storage.get(GossipPullActions.DATA_SET);
                hs.add(currentMessage);
                oldDatas.add(currentMessage);
            }
        }
    }


    public static class AddToHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            //check if this the first hello message
            if (!storage.containsKey(GossipPullActions.HELLO_SET)){
                storage.put(GossipPullActions.HELLO_SET, new HashSet<Message>());
            }
            HashSet<Message> hs = (HashSet<Message>)storage.get(GossipPullActions.HELLO_SET);
            hs.add((Message)storage.get(GossipPullActions.CURRENT_MESSAGE));
        }
    }

    public static class FlushHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            //check if this the first hello message
            if (!storage.containsKey(GossipPullActions.HELLO_SET)){
                storage.put(GossipPullActions.HELLO_SET, new HashSet<Message>());
            }
            HashSet<Message> hs = (HashSet<Message>)storage.get(GossipPullActions.HELLO_SET);
            hs.clear();
        }
    }

    public static class SendProbeToRandomNeighbors extends StateAdapter<INode> {

        private Random rand = new Random();
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {

            Map<String,Object> storage = context.getStorage();
            if (!storage.containsKey(GossipPullActions.HELLO_SET)){
                return;
            }
            HashSet<Message> hs = (HashSet<Message>)storage.get(GossipPullActions.HELLO_SET);
            ArrayList<Message> al = new ArrayList<Message>(hs);

            Message selected = al.get(rand.nextInt(al.size()));
            double distance = context.getPosition().getNorm(selected.getSenderPosition());
            double power = distance/context.getDescriptor().getEmissionRange();
            double transmissionPower = 0;
            context.send(transmissionPower, context.createMessage(Message.Type.PROBE));
        }
    }

}
