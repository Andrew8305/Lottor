package com.blueskykong.tm.server.config;

import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.netty.bean.HeartBeat;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.socket.SocketManager;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Objects;


public class ExecutorMessageTool {


    public static HeartBeat buildMessage(TxTransactionItem item, ChannelSender channelSender, TransactionStatusEnum transactionStatusEnum) {
        HeartBeat heartBeat = new HeartBeat();
        Channel channel = SocketManager.getInstance().getChannelByModelName(item.getModelName());
        if (Objects.nonNull(channel)) {
            if (channel.isActive()) {
                channelSender.setChannel(channel);
            }
        }
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        if (TransactionStatusEnum.ROLLBACK.getCode() == transactionStatusEnum.getCode()) {
            heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
            item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        } else if (TransactionStatusEnum.COMMIT.getCode() == transactionStatusEnum.getCode()) {
            heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
            item.setStatus(TransactionStatusEnum.COMMIT.getCode());
            txTransactionGroup.setStatus(TransactionStatusEnum.COMMIT.getCode());
        }
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        return heartBeat;
    }
}