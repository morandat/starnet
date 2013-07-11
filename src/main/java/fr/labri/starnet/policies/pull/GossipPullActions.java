package fr.labri.starnet.policies.pull;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.starnet.policies.commons.BasicActions;
import fr.labri.starnet.policies.commons.DataSet;
import fr.labri.starnet.policies.commons.HelloSet;
import fr.labri.starnet.policies.commons.CommonVar;
import fr.labri.timedautomata.ITimedAutomata;
import fr.labri.timedautomata.TimedAutomata.StateAdapter;

import java.util.*;


public class GossipPullActions {

    public static final String OLD_DATA_SET ="old_data_set";

    private static final long HELLO_MESSAGE_LIFETIME = 25;



    public static class InitEnv extends BasicActions.InitEnv {
        @Override
        public void preAction(INode context, ITimedAutomata<INode> auto) {
            super.preAction(context, auto);
            Map<String,Object> storage = context.getStorage();
            storage.put(GossipPullActions.OLD_DATA_SET, new DataSet());

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

    public static class AddToDataSet extends StateAdapter<INode> {
        @Override
        public void postAction(INode context, ITimedAutomata<INode> auto) {
            Map<String,Object> storage = context.getStorage();
            Message currentMessage = (Message)storage.get(CommonVar.CURRENT_MESSAGE);

            // check if already received message
            DataSet oldDatas = (DataSet)storage.get(GossipPullActions.OLD_DATA_SET);
            if (!oldDatas.contains(currentMessage)){
                //if not already received add to the current data set
                DataSet ds = (DataSet)storage.get(CommonVar.DATA_SET);
                ds.add(currentMessage);
                oldDatas.add(currentMessage);
            }
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
