package fr.labri.starnet.policies.pull;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.starnet.policies.commons.actions.CommonVar;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

import java.util.*;


public class GossipPullActions {

    public static final String OLD_DATA_SET ="old_data_set";

    private static final long HELLO_MESSAGE_LIFETIME = 25;



    public static class InitEnv extends StateAdapter<INode> {
        @Override
        public void preAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            storage.put(CommonVar.CURRENT_MESSAGE, null);
            storage.put(CommonVar.SAVED_MAILBOX, new ArrayDeque<Message>());
            storage.put(CommonVar.HELLO_SET, new HelloSet());
            storage.put(CommonVar.DATA_SET, new DataSet());
            storage.put(GossipPullActions.OLD_DATA_SET, new DataSet());

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
            Message currentMessage = (Message)storage.get(CommonVar.CURRENT_MESSAGE);

            double distance = context.getPosition().getNorm(currentMessage.getSenderPosition());
            double transmissionPower = distance/context.getDescriptor().getEmissionRange();

            DataSet datas = (DataSet)storage.get(CommonVar.DATA_SET);
            for (Message message : datas.getAll()) {
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
            storage.put(CommonVar.SAVED_MAILBOX, stack);
        }
    }

    public static class AddToMsgDataSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(CommonVar.CURRENT_MESSAGE);

            // check if already received message
            ArrayList<Message> oldDatas = (ArrayList<Message>)storage.get(GossipPullActions.OLD_DATA_SET);
            if (!oldDatas.contains(currentMessage)){
                //if not already received add to the current data set
                DataSet ds = (DataSet)storage.get(CommonVar.DATA_SET);
                ds.add(currentMessage);
                oldDatas.add(currentMessage);
            }
        }
    }


    public static class AddToHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(CommonVar.CURRENT_MESSAGE);
            HelloSet hs = (HelloSet)storage.get(CommonVar.HELLO_SET);
            hs.add(currentMessage);
        }
    }

    public static class CleanHelloSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            HelloSet hs = (HelloSet)storage.get(CommonVar.HELLO_SET);
            hs.clean(HELLO_MESSAGE_LIFETIME, context.getTime());
        }
    }

    public static class SendProbeToRandomNeighbors extends StateAdapter<INode> {

        private Random rand = new Random();
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {

            Map<String,Object> storage = context.getStorage();

            HelloSet hs = (HelloSet)storage.get(CommonVar.HELLO_SET);
            Message selected = hs.get(rand.nextInt(hs.size()));
            double distance = context.getPosition().getNorm(selected.getSenderPosition());
            double transmissionPower = distance/context.getDescriptor().getEmissionRange();
            context.send(transmissionPower, context.createMessage(Message.Type.PROBE));
        }
    }

}
