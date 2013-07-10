package fr.labri.starnet.policies.pull;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;


public class GossipPullActions {

    public static final String HELLO_SET = "hello_set";
    public static final String SAVED_MAILBOX = "saved_mailbox";
    public static final String CURRENT_MESSAGE = "current";
    public static final String DATA_SET ="data_set";
    public static final String OLD_DATA_SET ="old_data_set";

    private static final long HELLO_MESSAGE_LIFETIME = 25;



    public static class InitEnv extends StateAdapter<INode> {
        @Override
        public void preAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            storage.put(GossipPullActions.CURRENT_MESSAGE, null);
            storage.put(GossipPullActions.SAVED_MAILBOX, new ArrayDeque<Message>());
            storage.put(GossipPullActions.HELLO_SET, new HelloSet());
            storage.put(GossipPullActions.DATA_SET, new ArrayList<Message>());
            storage.put(GossipPullActions.OLD_DATA_SET, new ArrayList<Message>());

        }
    }

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

            ArrayList<Message> datas = (ArrayList<Message>)storage.get(GossipPullActions.DATA_SET);
            for (Message message : datas) {
                context.send(transmissionPower, context.forwardMessage(message));
            }
            datas.clear();
        }
    }

    public static class SaveMailbox extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            List<Message> list = Arrays.asList(context.receive());
            ArrayDeque<Message> stack = new ArrayDeque<Message>();
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
            ArrayList<Message> oldDatas = (ArrayList<Message>)storage.get(GossipPullActions.OLD_DATA_SET);
            if (!oldDatas.contains(currentMessage)){
                //if not already received add to the current data set
                ArrayList<Message> hs = (ArrayList<Message>)storage.get(GossipPullActions.DATA_SET);
                hs.add(currentMessage);
                oldDatas.add(currentMessage);
            }
        }
    }


    public static class AddToHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(GossipPullActions.CURRENT_MESSAGE);
            HelloSet hs = (HelloSet)storage.get(GossipPullActions.HELLO_SET);
            hs.add(currentMessage);
        }
    }

    public static class CleanHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            HelloSet hs = (HelloSet)storage.get(GossipPullActions.HELLO_SET);
            hs.clean(HELLO_MESSAGE_LIFETIME, context.getTime());
        }
    }

    public static class SendProbeToRandomNeighbors extends StateAdapter<INode> {

        private Random rand = new Random();
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {

            Map<String,Object> storage = context.getStorage();

            HelloSet hs = (HelloSet)storage.get(GossipPullActions.HELLO_SET);
            Message selected = hs.get(rand.nextInt(hs.size()));
            double distance = context.getPosition().getNorm(selected.getSenderPosition());
            double transmissionPower = distance/context.getDescriptor().getEmissionRange();
            context.send(transmissionPower, context.createMessage(Message.Type.PROBE));
        }
    }

}
